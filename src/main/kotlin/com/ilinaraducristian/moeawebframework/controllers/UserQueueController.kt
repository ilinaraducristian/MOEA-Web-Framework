package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.ProblemDTO
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.services.ProblemSolverService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("user/queue")
class UserQueueController(
    private val problemSolverService: ProblemSolverService,
    private val userRepo: UserRepository,
    private val problemRepo: ProblemRepository
) {

  @PostMapping("addProblem")
  fun addProblem(@Valid @RequestBody problem: ProblemDTO, principal: Principal): Mono<String> {
    return Mono.create<String> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty)
        return@create it.error(UserNotFoundException())
      val user = foundUser.get()
      if (problemRepo.existsByUserAndUserDefinedName(user, problem.userDefinedName))
        return@create it.error(ProblemExistsException())
      val newProblem = Problem(userDefinedName = problem.userDefinedName, name = problem.name, algorithm = problem.algorithm, numberOfEvaluations = problem.numberOfEvaluations, numberOfSeeds = problem.numberOfSeeds)
      newProblem.status = "waiting"
      newProblem.user = user
      val savedProblem = problemRepo.save(newProblem)
      it.success(""" {"id": "${savedProblem.id}"} """)
    }
  }

  @GetMapping("solveProblem/{id}")
  fun solveProblem(@PathVariable id: Long, principal: Principal): Mono<String> {
    return Mono.create<Problem> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty)
        return@create it.error(UserNotFoundException())
      val user = foundUser.get()
      val foundProblem = problemRepo.findByUserAndId(user, id)
      if (foundProblem.isEmpty)
        return@create it.error(ProblemNotFoundException())
      val problem = foundProblem.get()
      if (problem.status == "done") {
        return@create it.error(ProblemSolvedException())
      }
      if (problem.status == "working") {
        return@create it.error(ProblemIsSolvingException())
      }
      problem.status = "working"
      problemRepo.save(problem)
      it.success(problem)
    }.map { problem ->
      problemSolverService.solveProblem(problem)
    }
  }

  @GetMapping("cancelProblem/{id}")
  fun cancelProblem(@PathVariable solverId: String) {
    problemSolverService.cancelProblem(UUID.fromString(solverId))
  }

}