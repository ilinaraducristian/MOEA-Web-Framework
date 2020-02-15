package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.QueueItemDTO
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.repositories.AlgorithmRepository
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.QueueItemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.services.QueueItemSolverService
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
    private val problemRepo: ProblemRepository,
    private val algorithmRepo: AlgorithmRepository,
    private val queueItemRepo: QueueItemRepository
) {

  @PostMapping("addQueueItem")
  fun addQueueItem(@Valid @RequestBody queueItemDTO: QueueItemDTO, principal: Principal): Mono<String> {
    return Mono.create<String> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty) {
        return@create it.error(UserNotFoundException())
      }
      val user = foundUser.get()
      val problem = problemRepo.findByUsersAndName(user, queueItemDTO.problem).orElse(null)
          ?: return@create it.error(ProblemNotFoundException())
      val algorithm = algorithmRepo.findByUsersAndName(user, queueItemDTO.algorithm).orElse(null)
          ?: return@create it.error(AlgorithmNotFoundException())
      val queueItem = QueueItem()
      queueItem.name = queueItemDTO.name
      queueItem.problem = problem
      queueItem.algorithm = algorithm
      queueItem.numberOfSeeds = queueItemDTO.numberOfSeeds
      queueItem.numberOfEvaluations = queueItemDTO.numberOfEvaluations
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
  fun solveProblem(@PathVariable rabbitId: String, principal: Principal): Mono<String> {
    return Mono.create<String> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty) {
        return@create it.error(UserNotFoundException())
      }
      val user = foundUser.get()
      val queueItem = user.queue.find {queueItem -> queueItem.rabbitId == rabbitId}?:return@create it.error(QueueItemNotFoundException())
      if (queueItem.status == "done") {
        return@create it.error(QueueItemSolvedException())
      }
      if (queueItem.status == "working") {
        return@create it.error(QueueItemIsSolvingException())
      }
      queueItem.status = "working"
      queueItem.solverId = queueItemSolverService.solveQueueItem(queueItem)
      queueItemRepo.save(queueItem)
      it.success("""{"solverId": "${queueItem.solverId}"}""")
    }
  }

  @GetMapping("cancelQueueItem/{solverId}")
  fun cancelQueueItem(@PathVariable solverId: String) {
    queueItemSolverService.cancelQueueItem(UUID.fromString(solverId))
  }

}