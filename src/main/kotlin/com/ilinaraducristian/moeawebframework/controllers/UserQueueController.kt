package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.QueueItemRequestDTO
import com.ilinaraducristian.moeawebframework.dto.QueueItemResponseDTO
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.entities.User
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.repositories.QueueItemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.services.QueueItemSolverService
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

//  @GetMapping
//  fun getQueue(principal: Principal): Mono<List<QueueItemResponseDTO>> {
//    return Mono.create<List<QueueItemResponseDTO>> {
//      val user = userRepo.findByUsername(principal.name)
//          ?: return@create it.error(UserNotFoundException())
//      it.success(user.queue.map { queueItem ->
//        val queueItemResponseDTO = QueueItemResponseDTO()
//        queueItemResponseDTO.name = queueItem.name
//        queueItemResponseDTO.numberOfEvaluations = queueItem.numberOfEvaluations
//        queueItemResponseDTO.numberOfSeeds = queueItem.numberOfSeeds
//        queueItemResponseDTO.status = queueItem.status
//        queueItemResponseDTO.rabbitId = queueItem.rabbitId
//        queueItemResponseDTO.results = queueItem.results
//        queueItemResponseDTO.problem = queueItem.problem
//        queueItemResponseDTO.algorithm = queueItem.algorithm
//        return@map queueItemResponseDTO
//      }.toList())
//    }
//  }

  @PostMapping("addQueueItem")
  fun addQueueItem(@Valid @RequestBody queueItemRequestDTO: QueueItemRequestDTO, principal: Principal): Mono<String> {
    return Mono.create<String> {
      val user = userRepo.findByUsername(principal.name) ?: return@create it.error(UserNotFoundException())
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
      it.success(""" {"rabbitId": "${queueItem.rabbitId}"} """)
    }
  }

  @GetMapping("solveQueueItem/{rabbitId}")
  fun solveQueueItem(@PathVariable rabbitId: String, principal: Principal): Mono<String> {
    return Mono.create<String> {
      val user = userRepo.findByUsername(principal.name) ?: return@create it.error(UserNotFoundException())
      val queueItem = user.queue.find { queueItem -> queueItem.rabbitId == rabbitId }
          ?: return@create it.error(QueueItemNotFoundException())
      if (queueItem.status == "done") {
        return@create it.error(QueueItemSolvedException())
      }
      if (queueItem.status == "working") {
        return@create it.error(QueueItemIsSolvingException())
      }
      queueItem.status = "working"
      queueItemSolverService.solveQueueItem(queueItem)
      queueItemRepo.save(queueItem)
      it.success()
    }
  }

  @GetMapping("cancelQueueItem/{solverId}")
  fun cancelQueueItem(@PathVariable rabbitId: String): Mono<Void> {
    return Mono.create<Void>{
      if(queueItemSolverService.cancelQueueItem(rabbitId)) {
        it.success()
      }else {
        it.error(QueueItemNotFoundException())
      }
    }
  }

  @Transactional
  @GetMapping("removeQueueItem/{rabbitId}")
  fun removeQueueItem(@PathVariable rabbitId: String, principal: Principal): Mono<Void> {
    return Mono.create<Void>{
      val queueItem = queueItemRepo.findByUserUsernameAndRabbitId(principal.name, rabbitId)
      if(queueItem == null) {
        it.error(QueueItemNotFoundException())
      }else {
        queueItemSolverService.cancelQueueItem(rabbitId)
        queueItemRepo.deleteByUserUsernameAndRabbitId(principal.name, rabbitId)
        it.success()
      }
    }
  }

}