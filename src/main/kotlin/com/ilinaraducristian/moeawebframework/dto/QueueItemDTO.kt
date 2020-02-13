package com.ilinaraducristian.moeawebframework.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class QueueItemDTO{

  @NotNull
  @NotBlank
  var name: String = ""

  @NotNull
  var numberOfEvaluations: Int = 10000
  var numberOfSeeds: Int = 10
  var problem: String = ""
  var algorithm: String = ""
}