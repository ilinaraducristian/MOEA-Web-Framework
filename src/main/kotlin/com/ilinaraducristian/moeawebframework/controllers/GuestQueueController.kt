package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.configurations.algorithms
import com.ilinaraducristian.moeawebframework.configurations.problems
import com.ilinaraducristian.moeawebframework.dto.QueueItemRequestDTO
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.services.QueueItemSolverService
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.lang.RuntimeException
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
  fun addQueueItem(@RequestBody @Valid queueItem: QueueItem): Mono<String> {
    return Mono.create {
      if (!problems.contains(queueItem.problem)) return@create it.error(ProblemNotFoundException())
      if (!algorithms.contains(queueItem.algorithm)) return@create it.error(AlgorithmNotFoundException())
      var queueItemUUID: UUID
      do {
        queueItemUUID = UUID.randomUUID()
      } while (redisTemplate.opsForValue().get(queueItemUUID.toString()).block() != null)
      queueItem.rabbitId = queueItemUUID.toString()
      redisTemplate.opsForValue().set(queueItem.rabbitId, queueItem).block()
      it.success(""" {"rabbitId": "${queueItem.rabbitId}"} """)
    }
  }

  @GetMapping("solveQueueItem/{rabbitId}")
  fun solveQueueItem(@PathVariable rabbitId: String): Mono<Any> {
    return redisTemplate.opsForValue().get(rabbitId)
        .switchIfEmpty(Mono.error<QueueItem>(QueueItemNotFoundException()))
        .flatMap { queueItem ->
          if (queueItem.status == "done")
            return@flatMap Mono.error<QueueItemSolvedException>(QueueItemSolvedException())
          if (queueItem.status == "working")
            return@flatMap Mono.error<QueueItemIsSolvingException>(QueueItemIsSolvingException())
          queueItem.status = "working"
          try {
            queueItemSolverService.solveQueueItem(queueItem)
          }catch(e: Exception) {
            if(e is ProblemNotFoundException || e is AlgorithmNotFoundException) {
              return@flatMap Mono.error<RuntimeException>(e)
            }
          }
          redisTemplate.opsForValue().set(rabbitId, queueItem).map { Mono.empty<Void>() }
        }
  }

  @GetMapping("cancelQueueItem/{rabbitId}")
  fun cancelQueueItem(@PathVariable rabbitId: String): Mono<Any> {
    return redisTemplate.opsForValue().get(rabbitId)
        .switchIfEmpty(Mono.error<QueueItem>(QueueItemNotFoundException()))
        .flatMap { queueItem ->
          if (queueItem.status != "working")
            return@flatMap Mono.just(QueueItemIsNotSolvingException())
          queueItemSolverService.cancelQueueItem(rabbitId)
          queueItem.results = ArrayList()
          queueItem.status = "waiting"
          redisTemplate.opsForValue().set(rabbitId, queueItem).flatMap { Mono.empty<Any>() }
        }
  }

  @GetMapping("removeQueueItem/{rabbitId}")
  fun removeQueueItem(@PathVariable rabbitId: String): Mono<Any> {
    return redisTemplate.opsForValue().get(rabbitId)
        .switchIfEmpty(Mono.error<QueueItem>(QueueItemNotFoundException()))
        .flatMap {
          queueItemSolverService.cancelQueueItem(rabbitId)
          redisTemplate.delete(rabbitId).then(redisTemplate.opsForValue().get(rabbitId)).subscribe {
            println(it)
          }
          Mono.empty<Any>()
        }
  }

}