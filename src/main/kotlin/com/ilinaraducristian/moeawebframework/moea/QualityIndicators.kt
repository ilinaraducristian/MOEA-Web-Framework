package com.ilinaraducristian.moeawebframework.moea

import com.fasterxml.jackson.annotation.JsonIgnore
import org.moeaframework.analysis.collector.Accumulator
import java.io.Serializable

// [Approximation Set, R1Indicator, AdditiveEpsilonIndicator, R2Indicator, GenerationalDistance, Hypervolume, Spacing, R3Indicator, InvertedGenerationalDistance, NFE, Elapsed Time, Contribution]
class QualityIndicators() : Serializable {
  @JsonIgnore
  var accumulator: Accumulator? = null
  var currentSeed: Int = 0

  //    var ApproximationSet: ArrayList<Double> = ArrayList(),
  var R1Indicator: ArrayList<Double> = ArrayList()
  var AdditiveEpsilonIndicator: ArrayList<Double> = ArrayList()
  var R2Indicator: ArrayList<Double> = ArrayList()
  var GenerationalDistance: ArrayList<Double> = ArrayList()
  var Hypervolume: ArrayList<Double> = ArrayList()
  var Spacing: ArrayList<Double> = ArrayList()
  var R3Indicator: ArrayList<Double> = ArrayList()
  var InvertedGenerationalDistance: ArrayList<Double> = ArrayList()
  var NFE: ArrayList<Int> = ArrayList()
  var ElapsedTime: ArrayList<Double> = ArrayList()
  var Contribution: ArrayList<Double> = ArrayList()

  constructor(accumulator: Accumulator) : this() {
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