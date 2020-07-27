package org.moeawebframework.moeawebframework.controllers

import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.exceptions.BadCredentialsException
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.validation.Valid


@RestController
@RequestMapping("user")
class UserController(
    private val userService: UserService
) {

  @PostMapping("signup")
  suspend fun signup(@RequestBody @Valid signupInfo: SignupInfoDTO) = mono{
    userService.signup(signupInfo)
  }

  @PostMapping("login")
  suspend fun login(@RequestBody @Valid userCredentials: UserCredentialsDTO) = mono{
    println("LOGIN?")
    userService.login(userCredentials)
    return@mono
  }

}