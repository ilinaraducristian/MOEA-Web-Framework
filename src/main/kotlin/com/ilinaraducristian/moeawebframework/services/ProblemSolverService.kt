package com.ilinaraducristian.moeawebframework.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.entities.ProblemSolver
import com.ilinaraducristian.moeawebframework.moea.ProblemSolverRunner
import com.ilinaraducristian.moeawebframework.moea.QualityIndicators
import com.ilinaraducristian.moeawebframework.repositories.ProblemSolverRepository
import org.moeaframework.util.progress.ProgressListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class ProblemSolverService(
    private val threadPoolTaskExecutor: ThreadPoolTaskExecutor,
    private val rabbitTemplate: RabbitTemplate,
    private val jsonConverter: ObjectMapper,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, ProblemSolver>,
    private val problemSolverRepo: ProblemSolverRepository
) {

  val solvers = HashMap<String, ProblemSolverRunner>()

  fun solveProblemSolver(problemSolver: ProblemSolver) {

    val routingKey: String

    if (problemSolver.user.username == "guest") {
      routingKey = "guest.${problemSolver.rabbitId}"
    } else {
      routingKey = "user.${problemSolver.user.username}.${problemSolver.rabbitId}"
    }

    val progressListener = ProgressListener { event ->
      if (!event.isSeedFinished)
        return@ProgressListener
      try {
        val qualityIndicators = QualityIndicators(event.executor.instrumenter.lastAccumulator)
        qualityIndicators.currentSeed = event.currentSeed - 1
        problemSolver.results.add(qualityIndicators)
        rabbitTemplate.convertAndSend(routingKey, jsonConverter.writeValueAsString(qualityIndicators))
      } catch (e: IllegalArgumentException) {
        // executor was canceled
      }
    }

    val problemSolverSolver = ProblemSolverRunner(problemSolver, progressListener)
    threadPoolTaskExecutor.submit {
      solvers[problemSolver.rabbitId] = problemSolverSolver
      var solved = false

      try {
        solved = problemSolverSolver.solve()
        if (solved) {
          problemSolver.status = "done"
          updateProblemSolver(problemSolver)
          rabbitTemplate.convertAndSend(routingKey, """{"status":"done"}""")
        }
      } catch (e: Exception) {
        rabbitTemplate.convertAndSend(routingKey, """{"error":"${e.message}"}""")
      }
      if (!solved) {
        problemSolver.results = ArrayList()
        problemSolver.status = "waiting"
      }
      updateProblemSolver(problemSolver)

    }
  }

  fun cancelProblemSolver(rabbitId: String): Boolean {
    val found = solvers[rabbitId]
    if (found != null) {
      found.cancel()
      solvers.remove(rabbitId)
      return true
    }
    return false
  }

  private fun updateProblemSolver(problemSolver: ProblemSolver) {
    if (problemSolver.user.username == "guest") {
      reactiveRedisTemplate.opsForValue().set(problemSolver.rabbitId, problemSolver).block()
    } else {
      problemSolverRepo.save(problemSolver)
    }
  }

}