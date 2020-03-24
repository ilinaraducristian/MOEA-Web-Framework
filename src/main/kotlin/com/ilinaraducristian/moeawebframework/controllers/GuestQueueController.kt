package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.QueueItemRequestDTO
import com.ilinaraducristian.moeawebframework.entities.Algorithm
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.QueueItemIsSolvingException
import com.ilinaraducristian.moeawebframework.exceptions.QueueItemNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.QueueItemSolvedException
import com.ilinaraducristian.moeawebframework.repositories.AlgorithmRepository
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.services.QueueItemSolverService
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.getAndAwait
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("queue")
@CrossOrigin
class GuestQueueController(
    private val redisTemplate: ReactiveRedisTemplate<String, QueueItem>,
    private val queueItemSolverService: QueueItemSolverService,
    private val userRepo: UserRepository,
    private val problemRepo: ProblemRepository,
    private val algorithmRepo: AlgorithmRepository
) {

  @PostMapping("addQueueItem", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
  fun addQueueItem(@RequestBody @Valid queueItemRequestDTO: QueueItemRequestDTO): Mono<String> {
    return Mono.create {
      val queueItem = QueueItem()
      queueItem.name = queueItemRequestDTO.name
      queueItem.problem = Problem(name = queueItemRequestDTO.problem)
      queueItem.algorithm = Algorithm(name = queueItemRequestDTO.algorithm)
      queueItem.numberOfSeeds = queueItemRequestDTO.numberOfSeeds
      queueItem.numberOfEvaluations = queueItemRequestDTO.numberOfEvaluations
      var queueItemUUID: UUID
      do {
        queueItemUUID = UUID.randomUUID()
      } while (redisTemplate.opsForValue().get(queueItemUUID.toString()).block() != null)
      queueItem.rabbitId = queueItemUUID.toString()
      redisTemplate.opsForValue().set(queueItem.rabbitId, queueItem).block()
      it.success(""" {"rabbitId": "${queueItem.rabbitId}"} """)
    }
  }

  @GetMapping("solveQueueItem/{rabbitId}", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun solveQueueItem(@PathVariable rabbitId: String): Mono<String> {
    return Mono.create<String> {
      val queueItem = redisTemplate.opsForValue().get(rabbitId).block()
          ?: return@create it.error(ProblemNotFoundException())

      if (queueItem.status == "done")
          return@create it.error(QueueItemSolvedException())
        if (queueItem.status == "working")
          return@create it.error(QueueItemIsSolvingException())
        queueItem.status = "working"
        val solverId = queueItemSolverService.solveQueueItem(queueItem, false)
        queueItem.solverId = solverId
        it.success(redisTemplate.opsForValue().set(rabbitId, queueItem).map {
          """{"solverId": "$solverId"}"""
        }.block())
      }

  }

  @GetMapping("getQueueItem/{rabbitId}")
  fun getQueueItem(@PathVariable rabbitId: String): Mono<QueueItem> {
    return redisTemplate.opsForValue().get(rabbitId).flatMap { queueItem ->
      if (queueItem == null)
        return@flatMap Mono.error<QueueItem>(QueueItemNotFoundException())
      return@flatMap Mono.just(queueItem)
    }
  }

  @PostMapping
  fun getQueue(@RequestBody rabbitIds: Array<String>): Mono<List<QueueItem>> {
    return Mono.create {
      val queueItems = rabbitIds.map { rabbitId ->
        redisTemplate.opsForValue().get(rabbitId).block()
      }.filterNotNull()

      it.success(queueItems)
    }
  }

  @GetMapping("cancelQueueItem/{solverId}")
  fun cancelQueueItem(@PathVariable solverId: String) {
    queueItemSolverService.cancelQueueItem(UUID.fromString(solverId))
  }

  @GetMapping("removeQueueItem/{rabbitId}")
  fun removeQueueItem(@PathVariable rabbitId: String): Mono<Boolean> {
    return redisTemplate.opsForValue().get(rabbitId).filter{ queueItem -> queueItem != null}.flatMap { queueItem ->
      if(queueItem.solverId != null)
        queueItemSolverService.cancelQueueItem(UUID.fromString(queueItem.solverId))
      redisTemplate.opsForValue().delete(rabbitId)
    }
  }

}