package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("user")
class UserController(
    private val userService: UserService
) {

  @GetMapping
  suspend fun getUserData(authentication: Authentication): Map<String, List<Any>> {
    val principal = authentication.principal as Jwt
    return userService.getUserData(principal.getClaim("sub"))
  }

}