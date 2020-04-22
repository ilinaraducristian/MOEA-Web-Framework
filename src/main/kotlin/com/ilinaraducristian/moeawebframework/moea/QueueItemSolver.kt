package com.ilinaraducristian.moeawebframework.moea

import com.ilinaraducristian.moeawebframework.configurations.algorithms
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

  private val instrumenter: Instrumenter = Instrumenter()
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
  private var executor: Executor

  init {
    executor = Executor()
        .withInstrumenter(instrumenter)
        .withMaxEvaluations(queueItem.numberOfEvaluations)
        .withProgressListener(listener)

    if (problems.contains(queueItem.problem)) {
      instrumenter.withProblem(queueItem.problem)
      try {
        instrumenter.withEpsilon(EpsilonHelper.getEpsilon(ProblemFactory.getInstance().getProblem(queueItem.problem)))
      } catch (e: Exception) {
      }
      executor.withProblem(queueItem.problem)
    } else {
      if (queueItem.user.username == "guest")
        throw ProblemNotFoundException()
      val problemFile = File("moeaData/${queueItem.user.username}/problems/${queueItem.problem}.class")
      val referenceSetFile = File("moeaData/${queueItem.user.username}/problems/${queueItem.problem}.class")
      if (!problemFile.exists()) {
        throw ProblemNotFoundException()
      }
      if (!referenceSetFile.exists()) {
        throw ReferenceSetNotFoundException()
      }
      val problem = URLClassLoader(arrayOf(problemFile.toURI().toURL())).loadClass(queueItem.problem).getDeclaredConstructor().newInstance() as Problem
      instrumenter
          .withProblem(problem)
          .withReferenceSet(referenceSetFile)
      try {
        instrumenter.withEpsilon(EpsilonHelper.getEpsilon(problem))
      } catch (e: Exception) {
      }
      executor.withProblem(problem)
    }

    if (algorithms.contains(queueItem.algorithm)) {
      executor.withAlgorithm(queueItem.algorithm)
    } else {
      if (queueItem.user.username == "guest")
        throw AlgorithmNotFoundException()
      val algorithmFile = File("moeaData/${queueItem.user.username}/algorithms/${queueItem.algorithm}.class")
      if (!algorithmFile.exists()) {
        throw AlgorithmNotFoundException()
      }
      val algorithm = URLClassLoader(arrayOf(algorithmFile.toURI().toURL())).loadClass(queueItem.algorithm).declaredConstructors[1]
      println(algorithm)
      executor.withAlgorithm("").usingAlgorithmFactory(
          object : AlgorithmFactory() {
            override fun getAlgorithm(name: String?, properties: Properties?, problem: Problem?): Algorithm {
              return algorithm.newInstance(properties, problem) as Algorithm
            }
          }
      )
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