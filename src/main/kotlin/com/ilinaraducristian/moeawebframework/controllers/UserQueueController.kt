package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.entities.ProblemSolver
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.repositories.ProblemSolverRepository
import com.ilinaraducristian.moeawebframework.security.UserPrincipal
import com.ilinaraducristian.moeawebframework.services.ProblemSolverService
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("user/queue")
class UserQueueController(
    private val problemSolverService: ProblemSolverService,
    private val problemSolverRepo: ProblemSolverRepository
) {

  @GetMapping
  fun getQueue(authentication: Authentication): Mono<List<ProblemSolver>> {
    return mono {
      val user = (authentication.principal as UserPrincipal).user
      user.queue
    }
  }

  @PostMapping("addProblemSolver")
  fun addProblemSolver(@Valid @RequestBody problemSolver: ProblemSolver, authentication: Authentication): Mono<String> {
    return mono {
      val user = (authentication.principal as UserPrincipal).user
      if (!user.problems.contains(problemSolver.problem)) {
        throw RuntimeException(ProblemNotFoundException)
      }
      if (!user.algorithms.contains(problemSolver.algorithm)) {
        throw RuntimeException(AlgorithmNotFoundException)
      }
      problemSolver.user = user

      var rabbitId: UUID
      do {
        rabbitId = UUID.randomUUID()
      } while (problemSolverRepo.existsByUserAndRabbitId(user, rabbitId.toString()))
      problemSolver.rabbitId = rabbitId.toString()
      problemSolverRepo.save(problemSolver)
      """ {"rabbitId": "${problemSolver.rabbitId}"} """
    }
  }

  @GetMapping("solveProblemSolver/{rabbitId}")
  fun solveProblemSolver(@PathVariable rabbitId: String, authentication: Authentication): Mono<Unit> {

    return mono {
      val user = (authentication.principal as UserPrincipal).user
      val problemSolver = user.queue.find { problemSolver -> problemSolver.rabbitId == rabbitId }
          ?: throw RuntimeException(ProblemSolverNotFoundException)
      if (problemSolver.status == "done") {
        throw RuntimeException(ProblemSolverSolvedException)
      }
      if (problemSolver.status == "working") {
        throw RuntimeException(ProblemSolverIsSolvingException)
      }
      problemSolver.status = "working"
      problemSolverService.solveProblemSolver(problemSolver)
      problemSolverRepo.save(problemSolver)
      return@mono
    }
  }

  @GetMapping("cancelProblemSolver/{rabbitId}")
  fun cancelProblemSolver(@PathVariable rabbitId: String): Mono<Unit> {
    return mono {
      if (!problemSolverService.cancelProblemSolver(rabbitId)) {
        throw RuntimeException(ProblemSolverNotFoundException)
      }
    }
  }

  @Transactional
  @GetMapping("removeProblemSolver/{rabbitId}")
  fun removeProblemSolver(@PathVariable rabbitId: String, principal: Principal): Mono<Unit> {
    return mono {
      val problemSolver = problemSolverRepo.findByUserUsernameAndRabbitId(principal.name, rabbitId)
      if (problemSolver == null) {
        throw RuntimeException(ProblemSolverNotFoundException)
      } else {
        problemSolverService.cancelProblemSolver(rabbitId)
        problemSolverRepo.deleteByUserUsernameAndRabbitId(principal.name, rabbitId)
        return@mono
      }
    }
  }

}