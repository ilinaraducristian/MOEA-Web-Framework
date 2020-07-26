package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.entities.UserDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.web.FilterChainProxy
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("user")
class UserController(
    private val userDao: UserDao
) {

  @PostMapping("login")
  fun login(/*@RequestBody user: UserCredentialsDTO*/): Mono<String> {
    return Mono.just("[ UserController ] login()")
  }

}