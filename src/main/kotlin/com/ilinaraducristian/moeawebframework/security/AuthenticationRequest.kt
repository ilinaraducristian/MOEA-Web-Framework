package com.ilinaraducristian.moeawebframework.security

import java.io.Serializable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class AuthenticationRequest : Serializable {

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