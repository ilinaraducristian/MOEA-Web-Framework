package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.moea.CustomInstrumenter
import org.moeaframework.Executor
import org.moeaframework.util.progress.ProgressListener
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class TestController {

  private val logger = LoggerFactory.getLogger(TestController::class.java)

  @GetMapping("testEndpoint")
  fun get(): String {
    val problemName = "DTLZ1_2"
//    val executor: Executor
//    val instrumenter: Instrumenter
    val algorithmName = "NSGAII"
    val numberOfEvaluations = 10000
    val numberOfSeeds = 10

    val instrumenter = CustomInstrumenter(problemName)

    val listener = ProgressListener{
      event ->
        println(event.percentComplete)
    }

    val executor = Executor()
        .withProblem(problemName)
        .withInstrumenter(instrumenter)
        .withAlgorithm(algorithmName)
        .withMaxEvaluations(numberOfEvaluations)
        .withProgressListener(listener)

    try {
      executor.runSeeds(numberOfSeeds)
    }catch(e: Exception) {

    }
    return "Problem added!"
  }

}