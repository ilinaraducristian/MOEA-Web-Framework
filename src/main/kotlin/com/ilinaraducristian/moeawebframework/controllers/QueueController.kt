package com.ilinaraducristian.moeawebframework.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.Problem
import com.ilinaraducristian.moeawebframework.dto.QualityIndicators
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import com.ilinaraducristian.moeawebframework.moea.ProblemSolver
import org.moeaframework.core.Solution
import org.moeaframework.util.progress.ProgressListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import java.util.*
import javax.validation.Valid
import kotlin.collections.ArrayList
import kotlin.math.abs

@RestController
@RequestMapping("queue")
class QueueController(
    val problemRedisTemplate: RedisTemplate<Long, Problem>,
    val rabbitTemplate: RabbitTemplate,
    val jsonConverter: ObjectMapper,
    val threadPoolTaskExecutor: ThreadPoolTaskExecutor
) {

  val solvers = ArrayList<ProblemSolver>()

  @PostMapping("addProblem")
  fun addProblem(@Valid @RequestBody problem: Problem): String {
    if (problem.numberOfEvaluations < 500) problem.numberOfEvaluations = 500
    if (problem.numberOfSeeds < 1) problem.numberOfSeeds = 1
    do {
      problem.id = abs(Random().nextLong())
    } while (problemRedisTemplate.opsForValue().get(problem.id) != null)
    problemRedisTemplate.opsForValue().setIfAbsent(problem.id, problem)
    return """ {"id": "${problem.id}"} """
  }

  @GetMapping("solveProblem/{id}")
  fun solveProblem(@PathVariable id: Long) {
    val problem = problemRedisTemplate.opsForValue().get(id) ?: throw ProblemNotFoundException()
    threadPoolTaskExecutor.submit {
      var lastSeed = 1
      val listener = ProgressListener { event ->
        if (event.currentSeed > lastSeed) {
          lastSeed = event.currentSeed
          val accumulator = event.executor.instrumenter.lastAccumulator

          try {
            val size = accumulator.size("NFE")-1
            val qualityIndicators = QualityIndicators(accumulator, size, lastSeed - 1)
            rabbitTemplate.convertAndSend(problem.id.toString(), jsonConverter.writeValueAsString(qualityIndicators))
          } catch (e: IllegalArgumentException) {
          }
        }
      }

      val problemSolver = ProblemSolver(problem, listener)
      synchronized(solvers) {
        solvers.add(problemSolver)
      }

      try {
        if(problemSolver.solve())
          rabbitTemplate.convertAndSend(problem.id.toString(), """{"status":"done"}""")
      } catch (e: Exception) {
        println(e.printStackTrace())
        rabbitTemplate.convertAndSend(problem.id.toString(), """{"error":"${e.message}"}""")
      }
    }

  }

  @GetMapping("cancelProblem/{id}")
  fun cancelProblem(@PathVariable id: Long) {
    val problem = problemRedisTemplate.opsForValue().get(id) ?: throw ProblemNotFoundException()
    synchronized(solvers) {
      val found = solvers.find { solver -> solver.problem.name == problem.name }
      if (found != null) {
        found.cancel()
        solvers.remove(found)
      }
    }
  }

}