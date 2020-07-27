package org.moeawebframework.moeawebframework.dto

import org.moeawebframework.moeawebframework.entities.User

data class RegisteredUserDTO(
    var username: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String? = null
) {
  constructor(user: User) : this(
      user.username,
      user.email,
      user.firstName,
      user.lastName
  )
}