package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.QueueItemRequestDTO
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.repositories.QueueItemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.services.QueueItemSolverService
import kotlinx.coroutines.reactor.mono
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("user/queue")
class UserQueueController(
    private val queueItemSolverService: QueueItemSolverService,
    private val userRepo: UserRepository,
    private val queueItemRepo: QueueItemRepository
) {

  @GetMapping
  fun getQueue(principal: Principal): Mono<List<QueueItem>> {
    return mono {
      val user = userRepo.findByUsername(principal.name) ?: throw UserNotFoundException()
      user.queue
    }
  }

  @PostMapping("addQueueItem")
  fun addQueueItem(@Valid @RequestBody queueItemRequestDTO: QueueItemRequestDTO, principal: Principal): Mono<String> {
    return mono {
      val user = userRepo.findByUsername(principal.name) ?: throw UserNotFoundException()
      if (!user.problems.contains(queueItemRequestDTO.problem)) {
        throw ProblemNotFoundException()
      }
      if (!user.algorithms.contains(queueItemRequestDTO.algorithm)) {
        throw AlgorithmNotFoundException()
      }
      val queueItem = QueueItem()
      queueItem.name = queueItemRequestDTO.name
      queueItem.problem = queueItemRequestDTO.problem
      queueItem.algorithm = queueItemRequestDTO.algorithm
      queueItem.numberOfSeeds = queueItemRequestDTO.numberOfSeeds
      queueItem.numberOfEvaluations = queueItemRequestDTO.numberOfEvaluations
      queueItem.user = user
      var rabbitId: UUID
      do {
        rabbitId = UUID.randomUUID()
      } while (queueItemRepo.existsByUserAndRabbitId(user, rabbitId.toString()))
      queueItem.rabbitId = rabbitId.toString()
      queueItemRepo.save(queueItem)
      """ {"rabbitId": "${queueItem.rabbitId}"} """
    }
  }

  @GetMapping("solveQueueItem/{rabbitId}")
  fun solveQueueItem(@PathVariable rabbitId: String, principal: Principal): Mono<Unit> {
    return mono {
      val user = userRepo.findByUsername(principal.name) ?: throw UserNotFoundException()
      val queueItem = user.queue.find { queueItem -> queueItem.rabbitId == rabbitId }
          ?: throw QueueItemNotFoundException()
      if (queueItem.status == "done") {
        throw QueueItemSolvedException()
      }
      if (queueItem.status == "working") {
        throw QueueItemIsSolvingException()
      }
      queueItem.status = "working"
      queueItemSolverService.solveQueueItem(queueItem)
      queueItemRepo.save(queueItem)
      return@mono
    }
  }

  @GetMapping("cancelQueueItem/{rabbitId}")
  fun cancelQueueItem(@PathVariable rabbitId: String): Mono<Unit> {
    return mono {
      if (!queueItemSolverService.cancelQueueItem(rabbitId)) {
        throw QueueItemNotFoundException()
      }
    }
  }

  @Transactional
  @GetMapping("removeQueueItem/{rabbitId}")
  fun removeQueueItem(@PathVariable rabbitId: String, principal: Principal): Mono<Unit> {
    return mono {
      val queueItem = queueItemRepo.findByUserUsernameAndRabbitId(principal.name, rabbitId)
      if (queueItem == null) {
        throw QueueItemNotFoundException()
      } else {
        queueItemSolverService.cancelQueueItem(rabbitId)
        queueItemRepo.deleteByUserUsernameAndRabbitId(principal.name, rabbitId)
        return@mono
      }
    }
  }

}