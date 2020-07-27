package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.entities.UserDao
import org.moeawebframework.moeawebframework.exceptions.BadCredentialsException
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.lang.RuntimeException
import javax.validation.Valid


@RestController
@RequestMapping("user")
class UserController(
    private val userDao: UserDao
) {

  @PostMapping("signup")
  fun signup(@RequestBody @Valid signupInfo: SignupInfoDTO): Mono<Void> {
    return userDao.save(User(signupInfo)).flatMap { Mono.empty<Void>() }
  }

  @PostMapping("login")
  fun login(@RequestBody @Valid userCredentials: UserCredentialsDTO): Mono<RegisteredUserDTO> {
    return userDao.findByUsername(userCredentials.username).switchIfEmpty {
      Mono.error(RuntimeException(UserNotFoundException))
    }.flatMap {
      if(it.password == userCredentials.password) {
        return@flatMap Mono.just(RegisteredUserDTO(it))
      }else {
        return@flatMap Mono.error<RegisteredUserDTO>(RuntimeException(BadCredentialsException))
      }
    }
  }

}