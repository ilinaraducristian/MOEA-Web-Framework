package com.ilinaraducristian.moeawebframework.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.QualityIndicators
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.moea.ProblemSolver
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
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
    private val reactiveRedisTemplate: ReactiveRedisTemplate<Long, Problem>,
    private val problemRepo: ProblemRepository
) {

  val solvers = HashMap<UUID, ProblemSolver>()

  fun solveProblem(problem: Problem, isUser: Boolean = true): String {
    var solverId: UUID
    do {
      solverId = UUID.randomUUID()
    } while (solvers.contains(solverId))
    val progressListener = ProgressListener { event ->
      if (!event.isSeedFinished)
        return@ProgressListener
      try {
        val qualityIndicators = QualityIndicators(event.executor.instrumenter.lastAccumulator, event.currentSeed - 1)
        problem.results?.add(qualityIndicators)
        if (isUser) {
          rabbitTemplate.convertAndSend("user.${problem.user.username}.${problem.id}", jsonConverter.writeValueAsString(qualityIndicators))
        } else {
          rabbitTemplate.convertAndSend("guest.${problem.id}", jsonConverter.writeValueAsString(qualityIndicators))
        }
      } catch (e: IllegalArgumentException) {
        // executor was canceled
      }
    }
    val problemSolver = ProblemSolver(problem, progressListener)
    solvers[solverId] = problemSolver
    var solved = false
    threadPoolTaskExecutor.submit {
      try {
        solved = problemSolver.solve()
        if (solved) {
          problem.status = "done"
          if (isUser) {
            problemRepo.save(problem)
            rabbitTemplate.convertAndSend("user.${problem.user.username}.${problem.id}", """{"status":"done"}""")
          } else {
            reactiveRedisTemplate.opsForValue().set(problem.id, problem).block()
            rabbitTemplate.convertAndSend("guest.${problem.id}", """{"status":"done"}""")
          }
        }
      } catch (e: Exception) {
        if (isUser) {
          rabbitTemplate.convertAndSend("user.${problem.user.username}.${problem.id}", """{"error":"${e.message}"}""")
        } else {
          rabbitTemplate.convertAndSend("guest.${problem.id}", """{"error":"${e.message}"}""")
        }
      } finally {
        if (!solved) {
          problem.results = null
          problem.status = "waiting"
        }
        if (isUser) {
          problemRepo.save(problem)
        } else {
          reactiveRedisTemplate.opsForValue().set(problem.id, problem)
        }
      }
    }
    return solverId.toString()
  }

  fun cancelProblem(solverId: UUID) {
    val found = solvers[solverId]
    if (found != null) {
      found.cancel()
      solvers.remove(solverId)
    }
  }

}