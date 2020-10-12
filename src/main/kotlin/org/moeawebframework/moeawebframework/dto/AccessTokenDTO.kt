package org.moeawebframework.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonAlias

data class AccessTokenDTO (

  val access_token: String,

  val expires_in: Int,

  val refresh_expires_in: Int,

  val refresh_token: String,

  val token_type: String,

  @JsonAlias("not-before-policy")
  val not_before_policy: Int,

  val session_state: String,

  val scope: String

)