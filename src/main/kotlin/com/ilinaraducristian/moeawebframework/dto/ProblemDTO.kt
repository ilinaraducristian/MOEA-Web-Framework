package com.ilinaraducristian.moeawebframework.dto

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class ProblemDTO {

  @NotNull
  @NotBlank
  var userDefinedName: String = ""

  @NotNull
  @NotBlank
  var name: String = ""

  @NotNull
  @NotBlank
  var algorithm: String = ""

  @NotNull
  @Min(500)
  var numberOfEvaluations: Int = 10000

  @NotNull
  @Min(1)
  var numberOfSeeds: Int = 10

}