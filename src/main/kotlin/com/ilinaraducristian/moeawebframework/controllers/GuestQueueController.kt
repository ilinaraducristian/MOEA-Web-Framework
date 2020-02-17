package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.QueueItemRequestDTO
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.repositories.AlgorithmRepository
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.services.QueueItemSolverService
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

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

  @PostMapping("addQueueItem")
  fun addQueueItem(@RequestBody queueItemRequestDTO: QueueItemRequestDTO): Mono<String> {
    return Mono.create<String> {
      val foundUser = userRepo.findByUsername("admin")
      if(foundUser.isEmpty) {
        return@create it.error(InternalErrorException())
      }
      val admin = foundUser.get()
      val problem = problemRepo.findByUsers(admin).find { problem -> problem.name == queueItemRequestDTO.problem}
          ?: return@create it.error(ProblemNotFoundException())
      val algorithm = algorithmRepo.findByUsers(admin).find { algorithm -> algorithm.name == queueItemRequestDTO.algorithm}
          ?: return@create it.error(AlgorithmNotFoundException())
      val queueItem = QueueItem()
      queueItem.name = queueItemRequestDTO.name
      queueItem.problem = problem
      queueItem.algorithm = algorithm
      queueItem.numberOfSeeds = queueItemRequestDTO.numberOfSeeds
      queueItem.numberOfEvaluations = queueItemRequestDTO.numberOfEvaluations
      var queueItemUUID: UUID
      do {
        queueItemUUID = UUID.randomUUID()
      } while (reactiveRedisTemplate.opsForValue().get(queueItemUUID.toString()).block() != null)
      queueItem.rabbitId = queueItemUUID.toString()
      reactiveRedisTemplate.opsForValue().set(queueItem.rabbitId, queueItem).block()
      it.success(""" {"rabbitId": "${queueItem.rabbitId}"} """)

    }
  }

  @GetMapping("solveQueueItem/{rabbitId}")
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

  @GetMapping("cancelQueueItem/{solverId}")
  fun cancelQueueItem(@PathVariable solverId: String) {
    queueItemSolverService.cancelQueueItem(UUID.fromString(solverId))
  }

}