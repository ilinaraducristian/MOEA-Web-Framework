package com.ilinaraducristian.moeawebframework.dto

import com.ilinaraducristian.moeawebframework.entities.ProblemSolver
import com.ilinaraducristian.moeawebframework.entities.User
import java.io.Serializable

class AuthenticationResponseDTO() : Serializable {

  var username: String = ""
  var email: String = ""
  var firstName: String = ""
  var lastName: String? = null
  var jwt: String = ""
  var problems: List<String> = mutableListOf()
  var algorithms: List<String> = mutableListOf()
  var queue: List<ProblemSolver> = listOf()

  constructor(user: User) : this() {
    username = user.username
    email = user.email
    firstName = user.firstName
    lastName = user.lastName
    problems = user.problems
    algorithms = user.algorithms
    queue = user.queue
  }

}