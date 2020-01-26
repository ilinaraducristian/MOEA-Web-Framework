package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.User
import com.ilinaraducristian.moeawebframework.exceptions.CannotCreateUserException
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("user")
class UserController(
    private val userRepo: UserRepository,
    private val encoder: BCryptPasswordEncoder
) {

  @PostMapping("register")
  fun registerUser(@Valid @RequestBody user: User) {
    user.password = encoder.encode(user.password)
    try {
      userRepo.save(user)
    } catch (e: Exception) {
      throw CannotCreateUserException()
    }
  }

}