package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.Problem
import com.ilinaraducristian.moeawebframework.moea.CustomInstrumenter
import org.moeaframework.Executor
import org.moeaframework.util.progress.ProgressListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("problem")
class MainController(
    val problemRedisTemplate: RedisTemplate<Long, Problem>,
    val rabbitTemplate: RabbitTemplate
) {

  @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
  fun newProblem(@Valid @RequestBody problem: Problem): String {
    do {
      problem.id = Random().nextLong()
    }while(problemRedisTemplate.opsForValue().get(problem.id) != null)
    problemRedisTemplate.opsForValue().set(problem.id, problem)
    return """ {"id": "${problem.id}"} """
  }

  @GetMapping("{id}")
  fun runProblem(@PathVariable id: Long): String {
    val problem = problemRedisTemplate.opsForValue().get(id) ?: return "Problem not found!"
    val instrumenter = CustomInstrumenter(problem.name)
    var lastProgress = 0
    val listener = ProgressListener{
      event ->
        val progress = (event.percentComplete*100).toInt()
      if(progress > lastProgress) {
        lastProgress = progress
        rabbitTemplate.convertAndSend(problem.id.toString(), """ Progress: $lastProgress % """)
      }
    }
    val executor = Executor()
        .withProblem(problem.name)
        .withInstrumenter(instrumenter)
        .withAlgorithm(problem.algorithm)
        .withMaxEvaluations(problem.numberOfEvaluations)
        .withProgressListener(listener)
    Thread{
      try {
        executor.runSeeds(problem.numberOfSeeds)
        rabbitTemplate.convertAndSend(problem.id.toString(), "Problem solved")
      }catch(e: Exception) {
        rabbitTemplate.convertAndSend(problem.id.toString(), "Error")
      }
    }.start()
    return "Problem running!"
  }

}