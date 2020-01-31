package com.ilinaraducristian.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import org.moeaframework.analysis.collector.Accumulator
import java.io.Serializable

// [Approximation Set, R1Indicator, AdditiveEpsilonIndicator, R2Indicator, GenerationalDistance, Hypervolume, Spacing, R3Indicator, InvertedGenerationalDistance, NFE, Elapsed Time, Contribution]
data class QualityIndicators(
    @JsonIgnore
    val accumulator: Accumulator,
    val currentSeed: Int,
//    var ApproximationSet: ArrayList<Double> = ArrayList(),
    val R1Indicator: ArrayList<Double> = ArrayList(),
    val AdditiveEpsilonIndicator: ArrayList<Double> = ArrayList(),
    val R2Indicator: ArrayList<Double> = ArrayList(),
    val GenerationalDistance: ArrayList<Double> = ArrayList(),
    val Hypervolume: ArrayList<Double> = ArrayList(),
    val Spacing: ArrayList<Double> = ArrayList(),
    val R3Indicator: ArrayList<Double> = ArrayList(),
    val InvertedGenerationalDistance: ArrayList<Double> = ArrayList(),
    val NFE: ArrayList<Int> = ArrayList(),
    val ElapsedTime: ArrayList<Double> = ArrayList(),
    val Contribution: ArrayList<Double> = ArrayList()
) : Serializable {
  init {
    val size = accumulator.size("NFE") - 1
    for (i: Int in 0..size) {
//      ApproximationSet.add(accumulator.get("Approximation Set", i) as Double)
      R1Indicator.add(accumulator.get("R1Indicator", i) as Double)
      AdditiveEpsilonIndicator.add(accumulator.get("AdditiveEpsilonIndicator", i) as Double)
      R2Indicator.add(accumulator.get("R2Indicator", i) as Double)
      GenerationalDistance.add(accumulator.get("GenerationalDistance", i) as Double)
      Hypervolume.add(accumulator.get("Hypervolume", i) as Double)
      Spacing.add(accumulator.get("Spacing", i) as Double)
      R3Indicator.add(accumulator.get("R3Indicator", i) as Double)
      InvertedGenerationalDistance.add(accumulator.get("InvertedGenerationalDistance", i) as Double)
      NFE.add(accumulator.get("NFE", i) as Int)
      ElapsedTime.add(accumulator.get("Elapsed Time", i) as Double)
      Contribution.add(accumulator.get("Contribution", i) as Double)
    }
  }
}