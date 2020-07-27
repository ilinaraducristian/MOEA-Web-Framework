package org.moeawebframework.moeawebframework.dto

import org.moeawebframework.moeawebframework.entities.User

data class RegisteredUserDTO (
    var username: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String? = null
) {
  constructor(user: User) {
    username = user.username
    email = user.email
    firstName = user.firstName
    lastName = user.lastName
  }
}