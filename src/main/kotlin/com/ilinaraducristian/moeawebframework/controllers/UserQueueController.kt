package com.ilinaraducristian.moeawebframework.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.Problem
import com.ilinaraducristian.moeawebframework.dto.QualityIndicators
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.moea.ProblemSolver
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.moeaframework.util.progress.ProgressListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.concurrent.ThreadLocalRandom
import javax.validation.Valid
import kotlin.math.abs

@RestController
@RequestMapping("userQueue")
class UserQueueController(
    private val redisTemplate: ReactiveRedisTemplate<Long, Problem>,
    private val rabbitTemplate: RabbitTemplate,
    private val jsonConverter: ObjectMapper,
    private val threadPoolTaskExecutor: ThreadPoolTaskExecutor,
    private val userRepo: UserRepository,
    private val problemRepo: ProblemRepository
) {

  val solvers = ArrayList<ProblemSolver>()

  @PostMapping("addProblem")
  fun addProblem(@Valid @RequestBody problem: Problem, principal: Principal): Mono<String> {
    return Mono.create<String> {
      if (problem.numberOfEvaluations < 500) problem.numberOfEvaluations = 500
      if (problem.numberOfSeeds < 1) problem.numberOfSeeds = 1
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty)
        return@create it.error(UserNotFoundException())
      val user = foundUser.get()
      if (problemRepo.existsByUserAndUserDefinedName(user, problem.userDefinedName))
        return@create it.error(ProblemExistsException())
      problem.status = "waiting"
      problem.user = user
      val savedProblem = problemRepo.save(problem)
      it.success(""" {"id": "${savedProblem.id}"} """)
    }
  }

  @GetMapping("solveProblem/{id}")
  fun solveProblem(@PathVariable id: Long, principal: Principal): Mono<Void> {
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
                rabbitTemplate.convertAndSend("""user.${problem.user.username}.${problem.id}""", jsonConverter.writeValueAsString(qualityIndicators))
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
              rabbitTemplate.convertAndSend("""user.${problem.user.username}.${problem.id}""", """{"status":"done"}""")
            }
          } catch (e: Exception) {
            redisTemplate.opsForValue().set(id, problem)
            rabbitTemplate.convertAndSend("""user.${problem.user.username}.${problem.id}""", """{"error":"${e.message}"}""")
          } finally {
            if (!solved) {
              problem.results = null
              problem.status = "waiting"
            }
            problemRepo.save(problem)
          }
        }
        it.success()
      }
    }
  }

  @GetMapping("cancelProblem/{id}")
  fun cancelProblem(@PathVariable id: Long, principal: Principal): Mono<Void> {
    return Mono.create<Problem> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty)
        return@create it.error(UserNotFoundException())
      val user = foundUser.get()
      val foundProblem = problemRepo.findByUserAndId(user, id)
      if (foundProblem.isEmpty)
        return@create it.error(ProblemNotFoundException())
      val problem = foundProblem.get()
      if (problem.status != "working")
        return@create it.error(ProblemIsNotSolvingException())
      problem.status = "waiting"
      problemRepo.save(problem)
      it.success(problem)
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