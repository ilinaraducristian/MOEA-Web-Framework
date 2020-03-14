package com.ilinaraducristian.moeawebframework.dto

import com.ilinaraducristian.moeawebframework.constraints.DefaultAlgorithmsConstraint
import com.ilinaraducristian.moeawebframework.constraints.DefaultProblemsConstraint
import com.ilinaraducristian.moeawebframework.constraints.QueueItemNameConstraint
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class QueueItemRequestDTO {

  @NotNull
  @NotBlank
  @QueueItemNameConstraint
  var name: String = ""
  @DefaultProblemsConstraint
  var problem: String = ""
  @DefaultAlgorithmsConstraint
  var algorithm: String = ""
  @Min(500)
  var numberOfEvaluations: Int = 10000
  @Min(1)
  var numberOfSeeds: Int = 10
}