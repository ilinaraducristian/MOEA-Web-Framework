package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.configurations.algorithms
import com.ilinaraducristian.moeawebframework.configurations.problems
import com.ilinaraducristian.moeawebframework.entities.ProblemSolver
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.services.ProblemSolverService
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid
import kotlin.collections.ArrayList

@RestController
@RequestMapping("queue")
@CrossOrigin
class GuestQueueController(
    private val redisTemplate: ReactiveRedisTemplate<String, ProblemSolver>,
    private val problemSolverService: ProblemSolverService
) {

  @PostMapping("addProblemSolver")
  fun addProblemSolver(@RequestBody @Valid problemSolver: ProblemSolver): Mono<String> {
    return mono {
      if (!problems.contains(problemSolver.problem)) throw RuntimeException(ProblemNotFoundException)
      if (!algorithms.contains(problemSolver.algorithm)) throw RuntimeException(AlgorithmNotFoundException)

      var problemSolverUUID: UUID
      do {
        problemSolverUUID = UUID.randomUUID()
      } while (redisTemplate.opsForValue().get(problemSolverUUID.toString()).block() != null)
      problemSolver.rabbitId = problemSolverUUID.toString()
      redisTemplate.opsForValue().set(problemSolver.rabbitId, problemSolver).block()
      """ {"rabbitId": "${problemSolver.rabbitId}"} """
    }
  }

  @GetMapping("solveProblemSolver/{rabbitId}")
  fun solveProblemSolver(@PathVariable rabbitId: String): Mono<Unit> {
    return mono {
      val problemSolver = redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull()
          ?: throw RuntimeException(ProblemSolverNotFoundException)
      if (problemSolver.status == "done")
        throw RuntimeException(ProblemSolverSolvedException)
      if (problemSolver.status == "working")
        throw RuntimeException(ProblemSolverIsSolvingException)
      problemSolver.status = "working"
      try {
        problemSolverService.solveProblemSolver(problemSolver)
      } catch (e: Exception) {
        if (e.message == ProblemNotFoundException || e.message == AlgorithmNotFoundException) {
          throw e
        }
      }
      redisTemplate.opsForValue().set(rabbitId, problemSolver).awaitFirst()
      return@mono
    }
  }

  @GetMapping("cancelProblemSolver/{rabbitId}")
  fun cancelProblemSolver(@PathVariable rabbitId: String): Mono<Any> {
    return mono {
      val problemSolver = redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull()
          ?: throw RuntimeException(ProblemSolverNotFoundException)
      if (problemSolver.status != "working")
        throw RuntimeException(ProblemSolverIsNotSolvingException)
      problemSolverService.cancelProblemSolver(rabbitId)
      problemSolver.results = ArrayList()
      problemSolver.status = "waiting"
      redisTemplate.opsForValue().set(rabbitId, problemSolver).awaitFirst()
      return@mono
    }
  }

  @GetMapping("removeProblemSolver/{rabbitId}")
  fun removeProblemSolver(@PathVariable rabbitId: String): Mono<Unit> {
    return mono {
      val problemSolver = redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull()
          ?: throw RuntimeException(ProblemSolverNotFoundException)
      problemSolverService.cancelProblemSolver(rabbitId)
      redisTemplate.delete(rabbitId).awaitFirst()
      return@mono
    }
  }

}