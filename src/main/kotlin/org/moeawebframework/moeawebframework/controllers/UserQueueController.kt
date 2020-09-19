package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dto.ProcessDTO
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("user/queue")
class UserQueueController(
    private val userService: UserService
) {

  @PostMapping
  suspend fun addProcess(@RequestBody processDTO: ProcessDTO, authentication: Authentication): String {
    val principal = authentication.principal as Jwt
    val username = principal.claims?.get("preferred_username") as String
    return userService.addProcess(username, processDTO)
  }

  @PostMapping("process/{rabbitId}")
  suspend fun process(authentication: Authentication, @PathVariable rabbitId: String) {
    userService.process(rabbitId)
  }

}