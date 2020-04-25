package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.configurations.algorithms
import com.ilinaraducristian.moeawebframework.configurations.problems
import com.ilinaraducristian.moeawebframework.dto.QueueItemRequestDTO
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.services.QueueItemSolverService
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid
import kotlin.collections.ArrayList

@RestController
@RequestMapping("queue")
@CrossOrigin
class GuestQueueController(
    private val redisTemplate: ReactiveRedisTemplate<String, QueueItem>,
    private val queueItemSolverService: QueueItemSolverService
) {

  @PostMapping("addQueueItem")
  fun addQueueItem(@RequestBody @Valid queueItemRequestDTO: QueueItemRequestDTO): Mono<String> {
    return mono {
      if (!problems.contains(queueItemRequestDTO.problem)) throw ProblemNotFoundException()
      if (!algorithms.contains(queueItemRequestDTO.algorithm)) throw AlgorithmNotFoundException()
      val queueItem = QueueItem()
      queueItem.name = queueItemRequestDTO.name
      queueItem.problem = queueItemRequestDTO.problem
      queueItem.algorithm = queueItemRequestDTO.algorithm
      queueItem.numberOfSeeds = queueItemRequestDTO.numberOfSeeds
      queueItem.numberOfEvaluations = queueItemRequestDTO.numberOfEvaluations
      var queueItemUUID: UUID
      do {
        queueItemUUID = UUID.randomUUID()
      } while (redisTemplate.opsForValue().get(queueItemUUID.toString()).block() != null)
      queueItem.rabbitId = queueItemUUID.toString()
      redisTemplate.opsForValue().set(queueItem.rabbitId, queueItem).block()
      """ {"rabbitId": "${queueItem.rabbitId}"} """
    }
  }

  @GetMapping("solveQueueItem/{rabbitId}")
  fun solveQueueItem(@PathVariable rabbitId: String): Mono<Unit> {
    return mono {
      val queueItem = redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull() ?: throw QueueItemNotFoundException()
      if (queueItem.status == "done")
        throw QueueItemSolvedException()
      if (queueItem.status == "working")
        throw QueueItemIsSolvingException()
      queueItem.status = "working"
      try {
        queueItemSolverService.solveQueueItem(queueItem)
      } catch (e: Exception) {
        if (e is ProblemNotFoundException || e is AlgorithmNotFoundException) {
          throw e
        }
      }
      redisTemplate.opsForValue().set(rabbitId, queueItem).awaitFirst()
      return@mono
    }
  }

  @GetMapping("cancelQueueItem/{rabbitId}")
  fun cancelQueueItem(@PathVariable rabbitId: String): Mono<Any> {
    return mono {
      val queueItem = redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull() ?: throw QueueItemNotFoundException()
      if (queueItem.status != "working")
        throw QueueItemIsNotSolvingException()
      queueItemSolverService.cancelQueueItem(rabbitId)
      queueItem.results = ArrayList()
      queueItem.status = "waiting"
      redisTemplate.opsForValue().set(rabbitId, queueItem).awaitFirst()
      return@mono
    }
  }

  @GetMapping("removeQueueItem/{rabbitId}")
  fun removeQueueItem(@PathVariable rabbitId: String): Mono<Unit> {
    return mono {
      val queueItem = redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull() ?: throw QueueItemNotFoundException()
      queueItemSolverService.cancelQueueItem(rabbitId)
      redisTemplate.delete(rabbitId).awaitFirst()
      return@mono
    }
  }

}