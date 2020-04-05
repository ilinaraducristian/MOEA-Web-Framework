package com.ilinaraducristian.moeawebframework.moea

import com.ilinaraducristian.moeawebframework.configurations.problems
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.ReferenceSetNotFoundException
import org.moeaframework.Executor
import org.moeaframework.Instrumenter
import org.moeaframework.analysis.sensitivity.EpsilonHelper
import org.moeaframework.core.Algorithm
import org.moeaframework.core.Problem
import org.moeaframework.core.spi.AlgorithmFactory
import org.moeaframework.core.spi.ProblemFactory
import org.moeaframework.util.progress.ProgressListener
import java.io.File
import java.net.URLClassLoader
import java.util.*


class QueueItemSolver(private val queueItem: QueueItem, listener: ProgressListener) {

  private val instrumenter: Instrumenter
  private var executor: Executor

  init {
    instrumenter = Instrumenter()
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

    executor = Executor()
        .withInstrumenter(instrumenter)
        .withMaxEvaluations(queueItem.numberOfEvaluations)
        .withProgressListener(listener)

    if (queueItem.user.username == "guest") {
      instrumenter.withProblem(queueItem.problem)
      try {
        instrumenter.withEpsilon(EpsilonHelper.getEpsilon(ProblemFactory.getInstance().getProblem(queueItem.problem)))
      } catch (e: Exception) {
      }
      executor = executor
          .withProblem(queueItem.problem)
          .withAlgorithm(queueItem.algorithm)
    } else {
      if (problems.contains(queueItem.problem)) {
        instrumenter.withProblem(queueItem.problem)
        try {
          instrumenter.withEpsilon(EpsilonHelper.getEpsilon(ProblemFactory.getInstance().getProblem(queueItem.problem)))
        } catch (e: Exception) {
        }
        executor = executor
            .withProblem(queueItem.problem)
            .withAlgorithm(queueItem.algorithm)
      } else {
        val problemFile = File("moeaData/${queueItem.user.username}/problems/${queueItem.problem}.class")
        val referenceSetFile = File("moeaData/${queueItem.user.username}/problems/${queueItem.problem}.class")
        val algorithmFile = File("moeaData/${queueItem.user.username}/algorithms/${queueItem.algorithm}.class")
        if (!problemFile.exists()) {
          throw ProblemNotFoundException()
        }
        if (!referenceSetFile.exists()) {
          throw ReferenceSetNotFoundException()
        }
        if (!algorithmFile.exists()) {
          throw AlgorithmNotFoundException()
        }
        val problem = URLClassLoader(arrayOf(problemFile.toURI().toURL())).loadClass(queueItem.problem).getDeclaredConstructor().newInstance() as Problem
        val algorithm = URLClassLoader(arrayOf(algorithmFile.toURI().toURL())).loadClass(queueItem.algorithm).getDeclaredConstructor()
        instrumenter
            .withProblem(problem)
            .withReferenceSet(referenceSetFile)
        try {
          instrumenter.withEpsilon(EpsilonHelper.getEpsilon(problem))
        } catch (e: Exception) {
        }
        executor.withProblem(problem).withAlgorithm("").usingAlgorithmFactory(
            object : AlgorithmFactory() {
              override fun getAlgorithm(name: String?, properties: Properties?, problem: Problem?): Algorithm {
                return algorithm.newInstance(properties, problem) as Algorithm
              }
            }
        )
      }
    }
  }

  fun solve(): Boolean {
    executor.runSeeds(queueItem.numberOfSeeds)
    return !executor.isCanceled
  }

  fun cancel() {
    executor.cancel()
  }

}