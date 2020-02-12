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
      val problem = problemRepo.findByUserAndName(user, queueItemDTO.problem)
      val algorithm = algorithmRepo.findByUserAndName(user, queueItemDTO.algorithm)
      if (problem.isEmpty) {
        return@create it.error(ProblemNotFoundException())
      }
      if (algorithm.isEmpty) {
        return@create it.error(AlgorithmNotFoundException())
      }
      val queueItem = QueueItem()
      queueItem.name = queueItemDTO.name
      queueItem.problem = problem.get()
      queueItem.algorithm = algorithm.get()
      queueItem.numberOfSeeds = queueItemDTO.numberOfSeeds
      queueItem.numberOfEvaluations = queueItemDTO.numberOfEvaluations
      var rabbitId: UUID
      do {
        rabbitId = UUID.randomUUID()
      } while (queueItemRepo.existsByUserAndRabbitId(user, rabbitId.toString()))
      queueItem.rabbitId = rabbitId.toString()
      user.queue.add(queueItem)
      queueItem.user = user
      userRepo.save(user)
      it.success(""" {"id": "${queueItem.rabbitId}"} """)
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
      val foundQueueItem = queueItemRepo.findByUserAndRabbitId(user, rabbitId)
      if (foundQueueItem.isEmpty) {
        return@create it.error(QueueItemNotFoundException())
      }
      val queueItem = foundQueueItem.get()
      if (queueItem.status == "done") {
        return@create it.error(QueueItemSolvedException())
      }
      if (queueItem.status == "working") {
        return@create it.error(QueueItemIsSolvingException())
      }
      queueItem.status = "working"
      queueItem.solverId = Optional.of(queueItemSolverService.solveQueueItem(queueItem))
      queueItemRepo.save(queueItem)
      it.success("""{"solverId": "${queueItem.solverId}"}""")
    }
  }

  @GetMapping("cancelQueueItem/{solverId}")
  fun cancelQueueItem(@PathVariable solverId: String) {
    queueItemSolverService.cancelQueueItem(UUID.fromString(solverId))
  }

}