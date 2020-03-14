package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.QueueItemRequestDTO
import com.ilinaraducristian.moeawebframework.entities.Algorithm
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.repositories.AlgorithmRepository
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.services.QueueItemSolverService
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid
import kotlin.reflect.KClass


@RestController
@RequestMapping("queue")
@CrossOrigin
class GuestQueueController(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, QueueItem>,
    private val queueItemSolverService: QueueItemSolverService,
    private val userRepo: UserRepository,
    private val problemRepo: ProblemRepository,
    private val algorithmRepo: AlgorithmRepository
) {

  @PostMapping("addQueueItem", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun addQueueItem(@RequestBody @Valid queueItemRequestDTO: QueueItemRequestDTO): Mono<String> {
    return Mono.create {

      val queueItem = QueueItem()
      queueItem.name = queueItemRequestDTO.name
      queueItem.problem = Problem(name = queueItemRequestDTO.problem)
      queueItem.algorithm = Algorithm(name = queueItemRequestDTO.algorithm)
      queueItem.numberOfSeeds = queueItemRequestDTO.numberOfSeeds
      queueItem.numberOfEvaluations = queueItemRequestDTO.numberOfEvaluations
      println(queueItemRequestDTO.name)
      var queueItemUUID: UUID
      do {
        queueItemUUID = UUID.randomUUID()
      } while (reactiveRedisTemplate.opsForValue().get(queueItemUUID.toString()).block() != null)
      queueItem.rabbitId = queueItemUUID.toString()
      reactiveRedisTemplate.opsForValue().set(queueItem.rabbitId, queueItem).block()
      it.success(""" {"rabbitId": "${queueItem.rabbitId}"} """)

    }
  }

  @GetMapping("solveQueueItem/{rabbitId}", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun solveQueueItem(@PathVariable rabbitId: String): Mono<String> {
    return reactiveRedisTemplate.opsForValue().get(rabbitId)
        .flatMap { queueItem ->
          Mono.create<Void> {
            if (queueItem == null)
              return@create it.error(ProblemNotFoundException())
            if (queueItem.status == "done")
              return@create it.error(QueueItemSolvedException())
            if (queueItem.status == "working")
              return@create it.error(QueueItemIsSolvingException())
            queueItem.status = "working"
            val solverId = queueItemSolverService.solveQueueItem(queueItem, false)
            queueItem.solverId = solverId
            it.success()
          }.then(reactiveRedisTemplate.opsForValue().set(rabbitId, queueItem)).thenReturn(queueItem)
        }.map { queueItem ->
          """{"solverId": "${queueItem.solverId}"}"""
        }
  }

  @GetMapping("getQueueItem/{rabbitId}")
  fun getQueueItem(@PathVariable rabbitId: String): Mono<QueueItem> {
    return reactiveRedisTemplate.opsForValue().get(rabbitId).flatMap { queueItem ->
      if (queueItem == null)
        return@flatMap Mono.error<QueueItem>(QueueItemNotFoundException())
      return@flatMap Mono.just(queueItem)
    }
  }

  @PostMapping
  fun getQueue(@RequestBody rabbitIds: Array<String>): Mono<List<QueueItem>> {
    return Mono.create {
      val queueItems = rabbitIds.map { rabbitId ->
        reactiveRedisTemplate.opsForValue().get(rabbitId).block()
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
    return reactiveRedisTemplate.opsForValue().get(rabbitId).filter{queueItem -> queueItem != null}.flatMap {queueItem ->
      queueItemSolverService.cancelQueueItem(UUID.fromString(queueItem.solverId))
      reactiveRedisTemplate.opsForValue().delete(rabbitId)
    }
  }

}