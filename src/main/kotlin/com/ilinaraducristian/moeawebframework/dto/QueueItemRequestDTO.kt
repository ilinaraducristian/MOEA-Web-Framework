package com.ilinaraducristian.moeawebframework.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class QueueItemRequestDTO{

  @NotNull
  @NotBlank
  var name: String = ""
  var problem: String = ""
  var algorithm: String = ""
  var numberOfEvaluations: Int = 10000
  var numberOfSeeds: Int = 10
}