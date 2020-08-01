package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.entities.ProblemSolver
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("user/queue")
class UserQueueController(
    private val userService: UserService
) {

  @GetMapping
  fun testF(authentication: Authentication): String {
    val principal = authentication.principal as Jwt
    println(principal.claims.get("email"))
    return "Works"
  }

  @PostMapping
  fun addProblemSolver(@RequestBody problemSolver: ProblemSolver, authentication: Authentication): String {
//    val user = (authentication.details as UserPrincipal).user
    val user = User()
    userService.addProblemSolver(user, problemSolver)
    return "String"
  }

}