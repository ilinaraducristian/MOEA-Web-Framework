package com.ilinaraducristian.moeawebframework.dto

import java.io.Serializable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class AuthenticationRequestDTO : Serializable {

  @NotBlank
  @NotNull
  var username: String = ""

  @NotBlank
  @NotNull
  var password: String = ""

  override fun toString(): String {
    return """{username: $username, password: $password}"""
  }

}