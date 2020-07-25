package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("user")
class UserController {

  @PostMapping("login")
  fun login(/*@RequestBody user: UserCredentialsDTO*/): Mono<String> {
    return Mono.just("Nu e nevoie")
  }

}