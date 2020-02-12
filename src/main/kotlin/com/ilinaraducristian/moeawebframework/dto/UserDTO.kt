package com.ilinaraducristian.moeawebframework.dto

import java.util.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class UserDTO {
  @NotNull
  @NotBlank
  var username: String = ""

  @NotNull
  @NotBlank
  var password: String = ""

  @NotNull
  @Email
  var email: String = ""

  @NotNull
  @NotBlank
  var firstName: String = ""

  var lastName: String? = null
}