package com.ilinaraducristian.moeawebframework.security

import java.io.Serializable

class AuthenticationResponse : Serializable {
  var jwt: String = ""

  constructor(jwt: String) {
    this.jwt = jwt
  }

  override fun toString(): String {
    return """{jwt: $jwt}"""
  }

}