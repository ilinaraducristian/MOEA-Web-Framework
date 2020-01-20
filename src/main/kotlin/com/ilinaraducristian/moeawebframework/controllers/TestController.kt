package com.ilinaraducristian.moeawebframework.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.util.progress.ProgressListener

@RestController
class TestController {

  @GetMapping("testEndpoint")
  fun get(): String {
    val problemName = "Belegundu"
    val executor: Executor
    val instrumenter: Instrumenter
    val algorithmName = "CMA-ES"
    val numberOfEvaluations = 10000
    val numberOfSeeds = 1

    instrumenter = Instrumenter()
        .withFrequency(100)
        .withProblem(problemName)

    instrumenter.attachHypervolumeCollector()
    instrumenter.attachGenerationalDistanceCollector()
    instrumenter.attachInvertedGenerationalDistanceCollector()
    instrumenter.attachSpacingCollector()
    instrumenter.attachAdditiveEpsilonIndicatorCollector()
    instrumenter.attachContributionCollector()
    instrumenter.attachR1Collector()
    instrumenter.attachR2Collector()
    instrumenter.attachR3Collector()
    instrumenter.attachEpsilonProgressCollector()
    instrumenter.attachAdaptiveMultimethodVariationCollector()
    instrumenter.attachAdaptiveTimeContinuationCollector()
    instrumenter.attachElapsedTimeCollector()
    instrumenter.attachApproximationSetCollector()
    instrumenter.attachPopulationSizeCollector()

    val listener = ProgressListener{
      println(it.percentComplete)
    }

    executor = Executor()
        .withProblem(problemName)
        .withInstrumenter(instrumenter)
        .withAlgorithm(algorithmName)
        .withMaxEvaluations(numberOfEvaluations)
        .withProgressListener(listener)
    executor.runSeeds(numberOfSeeds)
    return "Endpoint works"
  }

}