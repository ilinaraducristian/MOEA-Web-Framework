package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.ProblemDTO
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.exceptions.ProblemIsSolvingException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemSolvedException
import com.ilinaraducristian.moeawebframework.services.ProblemSolverService
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs

@RestController
@RequestMapping("queue")
class GuestQueueController(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<Long, Problem>,
    private val problemSolverService: ProblemSolverService
) {

  @PostMapping("addProblem")
  fun addProblem(@RequestBody problem: ProblemDTO): Mono<String> {
    return Mono.create<String> {
      val savedProblem = Problem(userDefinedName = problem.userDefinedName, name = problem.name, algorithm = problem.algorithm, numberOfSeeds = problem.numberOfSeeds, numberOfEvaluations = problem.numberOfEvaluations)
      do {
        savedProblem.id = abs(ThreadLocalRandom.current().nextLong())
      } while (reactiveRedisTemplate.opsForValue().get(savedProblem.id).block() != null)
      reactiveRedisTemplate.opsForValue().set(savedProblem.id, savedProblem).block()
      it.success(""" {"id": "${savedProblem.id}"} """)
    }
  }

  @GetMapping("solveProblem/{id}")
  fun solveProblem(@PathVariable id: Long): Mono<String> {
    return reactiveRedisTemplate.opsForValue().get(id)
        .flatMap { problem ->
          Mono.create<Void> {
            if (problem == null)
              return@create it.error(ProblemNotFoundException())
            if (problem.status == "done")
              return@create it.error(ProblemSolvedException())
            if (problem.status == "working")
              return@create it.error(ProblemIsSolvingException())
            problem.status = "working"
            it.success()
          }.then(reactiveRedisTemplate.opsForValue().set(id, problem)).thenReturn(problem)
        }.map { problem ->
          problemSolverService.solveProblem(problem, false)
        }
  }

  @GetMapping("cancelProblem/{id}")
  fun cancelProblem(@PathVariable solverId: String) {
    problemSolverService.cancelProblem(UUID.fromString(solverId))
  }

}