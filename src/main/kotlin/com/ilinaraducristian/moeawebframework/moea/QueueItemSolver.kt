package com.ilinaraducristian.moeawebframework.moea

import com.ilinaraducristian.moeawebframework.controllers.TestController
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import org.moeaframework.Executor
import org.moeaframework.Instrumenter
import org.moeaframework.analysis.sensitivity.EpsilonHelper
import org.moeaframework.core.spi.ProblemFactory
import org.moeaframework.util.progress.ProgressListener
import org.slf4j.LoggerFactory

class QueueItemSolver(val queueItem: QueueItem, listener: ProgressListener) {

  private val logger = LoggerFactory.getLogger(QueueItemSolver::class.java)

  private val instrumenter: Instrumenter = Instrumenter()
      .withProblem(queueItem.problem.name)
      .withFrequency(1)
      .attachHypervolumeCollector()
      .attachGenerationalDistanceCollector()
      .attachInvertedGenerationalDistanceCollector()
      .attachSpacingCollector()
      .attachAdditiveEpsilonIndicatorCollector()
      .attachContributionCollector()
      .attachR1Collector()
      .attachR2Collector()
      .attachR3Collector()
      .attachEpsilonProgressCollector()
      .attachAdaptiveMultimethodVariationCollector()
      .attachAdaptiveTimeContinuationCollector()
      .attachElapsedTimeCollector()
      .attachApproximationSetCollector()
      .attachPopulationSizeCollector()
  private val executor: Executor
  private var solved: Boolean = false

  init {
    logger.info("QUEUE ITEM PROBLEM NAME " + queueItem.problem.name)
    var tmpProblem: org.moeaframework.core.Problem? = null
    try {
      tmpProblem = ProblemFactory.getInstance().getProblem(
          queueItem.problem.name)
      instrumenter.withEpsilon(EpsilonHelper.getEpsilon(
          tmpProblem))
    } finally {
      tmpProblem?.close()
    }
    executor = Executor()
        .withProblem(queueItem.problem.name)
        .withInstrumenter(instrumenter)
        .withAlgorithm(queueItem.algorithm.name)
        .withMaxEvaluations(queueItem.numberOfEvaluations)
        .withProgressListener(listener)
  }

  fun solve(): Boolean {
    solved = true
    executor.runSeeds(queueItem.numberOfSeeds)
    return solved
  }

  fun cancel() {
    solved = false
    executor.cancel()
  }

}