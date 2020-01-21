package com.ilinaraducristian.moeawebframework.moea

import org.moeaframework.Instrumenter
import org.moeaframework.analysis.sensitivity.EpsilonHelper
import org.moeaframework.core.Problem
import org.moeaframework.core.spi.ProblemFactory


class CustomInstrumenter(problemName: String): Instrumenter() {
  init {
		withProblem(problemName)
		withFrequency(100)
		attachHypervolumeCollector()
		attachGenerationalDistanceCollector()
		attachInvertedGenerationalDistanceCollector()
		attachSpacingCollector()
		attachAdditiveEpsilonIndicatorCollector()
		attachContributionCollector()
		attachR1Collector()
		attachR2Collector()
		attachR3Collector()
		attachEpsilonProgressCollector()
		attachAdaptiveMultimethodVariationCollector()
		attachAdaptiveTimeContinuationCollector()
		attachElapsedTimeCollector()
		attachApproximationSetCollector()
		attachPopulationSizeCollector()
    var problem: Problem? = null;
		try {
			problem = ProblemFactory.getInstance().getProblem(
					problemName);
			withEpsilon(EpsilonHelper.getEpsilon(
					problem));
		} finally {
			problem?.close()
		}
  }
}