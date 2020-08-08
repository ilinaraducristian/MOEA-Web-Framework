package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dto.ProcessDTO
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("user/queue")
class UserQueueController(
    private val userService: UserService
) {

  @PostMapping
  fun addProcess(@RequestBody processDTO: ProcessDTO, authentication: Authentication): Mono<String> {
    val principal = authentication.principal as Jwt
    val username = principal.claims?.get("preferred_username") as String
    return userService.addProcess(username, processDTO)
  }

  @PostMapping("process/{rabbitId}")
  fun process(authentication: Authentication, @PathVariable rabbitId: String): Mono<Unit> {
    return userService.process(rabbitId)
  }

}