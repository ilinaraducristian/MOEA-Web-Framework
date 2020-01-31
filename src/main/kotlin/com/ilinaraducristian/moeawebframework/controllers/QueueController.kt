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
@RequestMapping("queue")
class QueueController(
    private val problemRedisTemplate: ReactiveRedisTemplate<Long, Problem>,
    private val rabbitTemplate: RabbitTemplate,
    private val jsonConverter: ObjectMapper,
    private val threadPoolTaskExecutor: ThreadPoolTaskExecutor,
    private val userRepo: UserRepository,
    private val problemRepo: ProblemRepository
) {

  val solvers = ArrayList<ProblemSolver>()

  @PostMapping("addProblem")
  fun addProblem(@Valid @RequestBody problem: Problem, principal: Principal?): Mono<String> {
    return Mono.create<String> {
      if (problem.numberOfEvaluations < 500) problem.numberOfEvaluations = 500
      if (problem.numberOfSeeds < 1) problem.numberOfSeeds = 1
      if (principal == null) {
        // Guest
        do {
          problem.id = abs(ThreadLocalRandom.current().nextLong())
        } while (problemRedisTemplate.opsForValue().get(problem.id).block() != null)
        problem.status = "waiting"
        problemRedisTemplate.opsForValue().set(problem.id, problem).block()
        it.success(""" {"id": "${problem.id}"} """)
      } else {
        // User
        val user = userRepo.findByUsername(principal.name) ?: return@create it.error(UserNotFoundException())
        if (problemRepo.existsByUserAndUserDefinedName(user, problem.userDefinedName))
          return@create it.error(ProblemExistsException())
        problem.status = "waiting"
        problem.user = user
        val savedProblem = problemRepo.save(problem)
        it.success(""" {"id": "${savedProblem.id}"} """)
      }
    }
  }

  @GetMapping("solveProblem/{id}")
  fun solveProblem(@PathVariable id: Long, principal: Principal?): Mono<Void> {
    val findProblem: Mono<Problem>
    if (principal == null) {
      // Guest
      findProblem = problemRedisTemplate.opsForValue().get(id)
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
            }.then(problemRedisTemplate.opsForValue().set(id, problem)).thenReturn(problem)
          }
    } else {
      // User
      findProblem = Mono.create<Problem> {
        val user = userRepo.findByUsername(principal.name) ?: return@create it.error(UserNotFoundException())
        val problem = problemRepo.findByUserAndId(user, id) ?: return@create it.error(ProblemNotFoundException())
        if (problem.status == "done") {
          return@create it.error(ProblemSolvedException())
        }
        if (problem.status == "working") {
          return@create it.error(ProblemIsSolvingException())
        }
        problem.status = "working"
        problemRepo.save(problem)
        it.success(problem)
      }
    }
    return findProblem
        .flatMap { problem ->
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
                    if (principal == null)
                    // Guest
                      rabbitTemplate.convertAndSend("""guest.${problem.id}""", jsonConverter.writeValueAsString(qualityIndicators))
                    else
                    // User
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
                  if (principal == null)
                    rabbitTemplate.convertAndSend("""guest.${problem.id}""", """{"status":"done"}""")
                  else
                    rabbitTemplate.convertAndSend("""user.${problem.user.username}.${problem.id}""", """{"status":"done"}""")
                }
              } catch (e: Exception) {
                problemRedisTemplate.opsForValue().set(id, problem)
                if (principal == null)
                  rabbitTemplate.convertAndSend("""guest.${problem.id}""", """{"error":"${e.message}"}""")
                else
                  rabbitTemplate.convertAndSend("""user.${problem.user.username}.${problem.id}""", """{"error":"${e.message}"}""")
              } finally {
                if (!solved) {
                  problem.results = null
                  problem.status = "waiting"
                }
                if (principal == null)
                  problemRedisTemplate.opsForValue().set(id, problem)
                else
                  problemRepo.save(problem)
              }
            }
            it.success()
          }
        }
  }

  @GetMapping("cancelProblem/{id}")
  fun cancelProblem(@PathVariable id: Long, principal: Principal?): Mono<Void> {
    val findProblem: Mono<Problem>
    if (principal == null) {
      findProblem = problemRedisTemplate.opsForValue().get(id).flatMap { problem ->
        Mono.create<Void> {
          if (problem == null)
            return@create it.error(ProblemNotFoundException())
          if (problem.status != "working")
            return@create it.error(ProblemIsNotSolvingException())
          problem.status = "waiting"
          it.success()
        }.then(problemRedisTemplate.opsForValue().set(id, problem)).thenReturn(problem)
      }
    } else {
      findProblem = Mono.create<Problem> {
        val user = userRepo.findByUsername(principal.name) ?: return@create it.error(UserNotFoundException())
        val problem = problemRepo.findByUserAndId(user, id) ?: return@create it.error(ProblemNotFoundException())
        if (problem.status != "working")
          return@create it.error(ProblemIsNotSolvingException())
        problem.status = "waiting"
        problemRepo.save(problem)
        it.success(problem)
      }
    }
    return findProblem.flatMap { problem ->
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