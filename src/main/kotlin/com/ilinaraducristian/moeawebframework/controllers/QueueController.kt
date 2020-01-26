package com.ilinaraducristian.moeawebframework.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.Problem
import com.ilinaraducristian.moeawebframework.dto.QualityIndicators
import com.ilinaraducristian.moeawebframework.exceptions.ProblemIsSolvingException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemSolvedException
import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
import com.ilinaraducristian.moeawebframework.moea.ProblemSolver
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.moeaframework.util.progress.ProgressListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*
import javax.validation.Valid
import kotlin.collections.ArrayList
import kotlin.math.abs

@RestController
@RequestMapping("queue")
class QueueController(
    val problemRedisTemplate: ReactiveRedisTemplate<Long, Problem>,
    val rabbitTemplate: RabbitTemplate,
    val jsonConverter: ObjectMapper,
    val threadPoolTaskExecutor: ThreadPoolTaskExecutor,
    val userRepo: UserRepository
) {

  val solvers = ArrayList<ProblemSolver>()

  @PostMapping("addProblem")
  fun addProblem(@Valid @RequestBody problem: Problem, principal: Principal?): Mono<String> {
    if (problem.numberOfEvaluations < 500) problem.numberOfEvaluations = 500
    if (problem.numberOfSeeds < 1) problem.numberOfSeeds = 1
    if (principal == null) {
      do {
        problem.id = abs(Random().nextLong())
      } while (problemRedisTemplate.opsForValue().get(problem.id).block() != null)
      problem.status = "waiting"
      return problemRedisTemplate.opsForValue().set(problem.id, problem).thenReturn(""" {"id": "${problem.id}"} """)
    } else {
      val user = userRepo.findByUsername(principal.name)
      if (user == null)
        throw UserNotFoundException()
      else
        return Mono.create<String> {
          user.problems.add(problem)
          val newUser = userRepo.save(user)
          it.success(""" {"id": "${newUser.problems.last().id}"} """)
        }
    }
  }

  @GetMapping("solveProblem/{id}")
  fun solveProblem(@PathVariable id: Long, principal: Principal?): Mono<Void> {
    fun startProblem(problem: Problem): Mono<Void> {
      return Mono.create<Void> {
        threadPoolTaskExecutor.submit {
          var lastSeed = 1
          val listener = ProgressListener { event ->
            if (event.currentSeed > lastSeed) {
              lastSeed = event.currentSeed
              val accumulator = event.executor.instrumenter.lastAccumulator

              try {
                val size = accumulator.size("NFE") - 1
                val qualityIndicators = QualityIndicators(accumulator, size, lastSeed - 1)

                problem.results?.add(qualityIndicators)
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
            if (problemSolver.solve()) {
              problem.status = "done"
              rabbitTemplate.convertAndSend(problem.id.toString(), """{"status":"done"}""")
            } else {
              problem.results = null
              problem.status = "waiting"
            }
            problemRedisTemplate.opsForValue().set(id, problem)
          } catch (e: Exception) {
            problem.results = null
            problem.status = "waiting"
            problemRedisTemplate.opsForValue().set(id, problem)
            rabbitTemplate.convertAndSend(problem.id.toString(), """{"error":"${e.message}"}""")
          }
        }
        it.success()
      }
    }

    val findProblem: Mono<Problem>
    if (principal == null)
      findProblem = problemRedisTemplate.opsForValue().get(id)
          .flatMap { problem ->
            Mono.create<Void> {
              if (problem == null) {
                it.error(ProblemNotFoundException())
              } else if (problem.status == "done") {
                it.error(ProblemSolvedException())
              } else if (problem.status == "working") {
                it.error(ProblemIsSolvingException())
              } else {
                problem.status = "working"
                problem.results = ArrayList()
                it.success()
              }

            }.then(problemRedisTemplate.opsForValue().set(id, problem)).thenReturn(problem)
          }
    else
      findProblem = Mono.create<Problem> {
        val user = userRepo.findByUsername(principal.name)
        if (user == null) {
          it.error(UserNotFoundException())
        } else {
          val problem: Problem? = user.problems.filter { problem -> problem.id == id }[0]
          if (problem == null) {
            it.error(ProblemNotFoundException())
          } else {
            it.success(problem)
          }
        }
      }
    return findProblem
        .flatMap(::startProblem)
        .doOnError { error ->
          throw error
        }
  }

  @GetMapping("cancelProblem/{id}")
  fun cancelProblem(@PathVariable id: Long, principal: Principal?): Mono<Void> {
    fun cancelFoundProblem(problem: Problem): Mono<Void> {
      return Mono.create<Void> {
        synchronized(solvers) {
          val found = solvers.find { solver -> solver.problem.name == problem.name }
          if (found != null) {
            found.cancel()
            solvers.remove(found)
          }
        }
        it.success()
      }
    }

    val findProblem: Mono<Problem>
    if (principal == null) {
      findProblem = problemRedisTemplate.opsForValue().get(id)
    } else {
      findProblem = Mono.create<Problem> {
        val user = userRepo.findByUsername(principal.name)
        if (user == null) {
          it.error(UserNotFoundException())
        } else {
          val problem: Problem? = user.problems.filter { problem -> problem.id == id }[0]
          if (problem == null) {
            it.error(ProblemNotFoundException())
          } else {
            it.success(problem)
          }
        }

      }
    }
    return findProblem.flatMap(::cancelFoundProblem)
        .doOnError { error ->
          throw error
        }
  }

}