package com.ilinaraducristian.moeawebframework.moea

import com.ilinaraducristian.moeawebframework.dto.Problem
import org.moeaframework.Executor
import org.moeaframework.Instrumenter
import org.moeaframework.analysis.sensitivity.EpsilonHelper
import org.moeaframework.core.spi.ProblemFactory
import org.moeaframework.util.progress.ProgressListener

class ProblemSolver(val problem: Problem, listener: ProgressListener) {

  private val instrumenter: Instrumenter = Instrumenter()
    .withProblem(problem.name)
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
    var tmpProblem: org.moeaframework.core.Problem? = null;
    try {
      tmpProblem = ProblemFactory.getInstance().getProblem(
          problem.name);
      instrumenter.withEpsilon(EpsilonHelper.getEpsilon(
          tmpProblem));
    } finally {
      tmpProblem?.close()
    }
    executor = Executor()
        .withProblem(problem.name)
        .withInstrumenter(instrumenter)
        .withAlgorithm(problem.algorithm)
        .withMaxEvaluations(problem.numberOfEvaluations)
        .withProgressListener(listener)
  }

  fun solve(): Boolean {
    solved = true
    executor.runSeeds(problem.numberOfSeeds)
    return solved
  }

  fun cancel() {
    solved = false
    executor.cancel()
  }

}