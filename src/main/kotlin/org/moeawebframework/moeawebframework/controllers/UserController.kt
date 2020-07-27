package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.entities.UserDao
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.validation.Valid


@RestController
@RequestMapping("user")
class UserController(
    private val userDao: UserDao
) {

  @PostMapping("signup")
  fun signup(@RequestBody @Valid signupInfo: SignupInfoDTO): Mono<RegisteredUserDTO> {
    val user = User(signupInfo)
    return userDao.save(user).map { RegisteredUserDTO(it) }
  }

  @PostMapping("login")
  fun login(@RequestBody @Valid user: UserCredentialsDTO): Mono<String> {
    return Mono.just("[ UserController ] login()")
  }

}