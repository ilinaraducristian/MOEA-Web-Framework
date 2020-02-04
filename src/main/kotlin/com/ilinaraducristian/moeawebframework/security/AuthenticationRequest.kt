package com.ilinaraducristian.moeawebframework.security

import java.io.Serializable

class AuthenticationRequest : Serializable {
  var username: String = ""
  var password: String = ""

  override fun toString(): String {
    return """{username: $username, password: $password}"""
  }

}