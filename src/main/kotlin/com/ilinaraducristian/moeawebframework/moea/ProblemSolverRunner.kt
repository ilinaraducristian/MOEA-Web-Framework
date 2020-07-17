package com.ilinaraducristian.moeawebframework.moea

import com.ilinaraducristian.moeawebframework.configurations.algorithms
import com.ilinaraducristian.moeawebframework.configurations.problems
import com.ilinaraducristian.moeawebframework.entities.ProblemSolver
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

class ProblemSolverRunner(private val problemSolver: ProblemSolver, listener: ProgressListener) {

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
        .withMaxEvaluations(problemSolver.numberOfEvaluations)
        .withProgressListener(listener)

    if (problems.contains(problemSolver.problem)) {
      instrumenter.withProblem(problemSolver.problem)
      try {
        instrumenter.withEpsilon(EpsilonHelper.getEpsilon(ProblemFactory.getInstance().getProblem(problemSolver.problem)))
      } catch (e: Exception) {
      }
      executor.withProblem(problemSolver.problem)
    } else {
      if (problemSolver.user.username == "guest")
        throw RuntimeException(ProblemNotFoundException)
      val problemFile = File("moeaData/${problemSolver.user.username}/problems/${problemSolver.problem}.class")
      val referenceSetFile = File("moeaData/${problemSolver.user.username}/problems/${problemSolver.problem}.class")
      if (!problemFile.exists()) {
        throw RuntimeException(ProblemNotFoundException)
      }
      if (!referenceSetFile.exists()) {
        throw RuntimeException(ReferenceSetNotFoundException)
      }
      val problem = URLClassLoader(arrayOf(problemFile.toURI().toURL())).loadClass(problemSolver.problem).getDeclaredConstructor().newInstance() as Problem
      instrumenter
          .withProblem(problem)
          .withReferenceSet(referenceSetFile)
      try {
        instrumenter.withEpsilon(EpsilonHelper.getEpsilon(problem))
      } catch (e: Exception) {
      }
      executor.withProblem(problem)
    }

    if (algorithms.contains(problemSolver.algorithm)) {
      executor.withAlgorithm(problemSolver.algorithm)
    } else {
      if (problemSolver.user.username == "guest")
        throw RuntimeException(AlgorithmNotFoundException)
      val algorithmFile = File("moeaData/${problemSolver.user.username}/algorithms/${problemSolver.algorithm}.class")
      if (!algorithmFile.exists()) {
        throw RuntimeException(AlgorithmNotFoundException)
      }
      val algorithm = URLClassLoader(arrayOf(algorithmFile.toURI().toURL())).loadClass(problemSolver.algorithm).declaredConstructors[1]
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
    executor.runSeeds(problemSolver.numberOfSeeds)
    return !executor.isCanceled
  }

  fun cancel() {
    executor.cancel()
  }

}