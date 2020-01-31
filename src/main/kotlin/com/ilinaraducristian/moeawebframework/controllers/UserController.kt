package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.Authority
import com.ilinaraducristian.moeawebframework.dto.User
import com.ilinaraducristian.moeawebframework.exceptions.CannotCreateUserException
import com.ilinaraducristian.moeawebframework.repositories.AuthorityRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("user")
class UserController(
    private val userRepo: UserRepository,
    private val authorityRepo: AuthorityRepository,
    private val encoder: BCryptPasswordEncoder
) {

  @PostMapping("register")
  fun register(@Valid @RequestBody user: User): Mono<Void> {
    return Mono.create<Void> {
      user.password = encoder.encode(user.password)
      try {
        println(userRepo.save(user))
        println(authorityRepo.save(Authority(user = user)))
        it.success()
      } catch (e: Exception) {
        it.error(CannotCreateUserException())
      }
    }
  }

}