package com.ilinaraducristian.moeawebframework.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.ProblemDTO
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.dto.QualityIndicators
import com.ilinaraducristian.moeawebframework.exceptions.ProblemIsNotSolvingException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemIsSolvingException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemSolvedException
import com.ilinaraducristian.moeawebframework.moea.ProblemSolver
import org.moeaframework.util.progress.ProgressListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs

@RestController
@RequestMapping("queue")
class GuestQueueController(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<Long, Problem>,
    private val rabbitTemplate: RabbitTemplate,
    private val jsonConverter: ObjectMapper,
    private val threadPoolTaskExecutor: ThreadPoolTaskExecutor
) {

  val solvers = ArrayList<ProblemSolver>()

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
  fun solveProblem(@PathVariable id: Long): Mono<Void> {
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
        }.flatMap { problem ->
          Mono.create<Void> {
            threadPoolTaskExecutor.submit {
              problem.results = ArrayList()
              var lastSeed = 1
              val listener = ProgressListener { event ->
                if (event.currentSeed > lastSeed) {
                  lastSeed = event.currentSeed
                  try {
                    val qualityIndicators = QualityIndicators(event.executor.instrumenter.lastAccumulator, lastSeed - 1)
                    problem.results?.add(qualityIndicators)
                    rabbitTemplate.convertAndSend("""guest.${problem.id}""", jsonConverter.writeValueAsString(qualityIndicators))
                  } catch (e: IllegalArgumentException) {
                    // executor was canceled
                  }
                }
              }

              val problemSolver = ProblemSolver(problem, listener)
              synchronized(solvers) {
                solvers.add(problemSolver)
              }

              var solved = false
              try {
                solved = problemSolver.solve()
                if (solved) {
                  problem.status = "done"
                  rabbitTemplate.convertAndSend("""guest.${problem.id}""", """{"status":"done"}""")
                }
              } catch (e: Exception) {
                reactiveRedisTemplate.opsForValue().set(id, problem)
                rabbitTemplate.convertAndSend("""guest.${problem.id}""", """{"error":"${e.message}"}""")
              } finally {
                if (!solved) {
                  problem.results = null
                  problem.status = "waiting"
                }
                reactiveRedisTemplate.opsForValue().set(id, problem)
              }
            }
            it.success()
          }
        }
  }

  @GetMapping("cancelProblem/{id}")
  fun cancelProblem(@PathVariable id: Long): Mono<Void> {
    return reactiveRedisTemplate.opsForValue().get(id).flatMap { problem ->
      Mono.create<Void> {
        if (problem == null)
          return@create it.error(ProblemNotFoundException())
        if (problem.status != "working")
          return@create it.error(ProblemIsNotSolvingException())
        problem.status = "waiting"
        it.success()
      }.then(reactiveRedisTemplate.opsForValue().set(id, problem)).thenReturn(problem)
    }.flatMap { problem ->
      // TODO Conflict problem, need multiple solvers arrays to isolate problem based on user
      Mono.create<Void> {
        synchronized(solvers) {
          val found = solvers.find { solver -> solver.problem.userDefinedName == problem.userDefinedName }
          if (found != null) {
            found.cancel()
            solvers.remove(found)
          }
        }
        it.success()
      }
    }
  }

}