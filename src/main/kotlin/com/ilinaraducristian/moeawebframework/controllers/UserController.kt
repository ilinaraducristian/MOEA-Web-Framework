package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.JwtUtil
import com.ilinaraducristian.moeawebframework.dto.UserDTO
import com.ilinaraducristian.moeawebframework.entities.Authority
import com.ilinaraducristian.moeawebframework.entities.User
import com.ilinaraducristian.moeawebframework.exceptions.BadCredentialsException
import com.ilinaraducristian.moeawebframework.exceptions.CannotCreateUserException
import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
import com.ilinaraducristian.moeawebframework.repositories.AuthorityRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.security.AuthenticationRequest
import com.ilinaraducristian.moeawebframework.security.AuthenticationResponse
import com.ilinaraducristian.moeawebframework.security.SecurityUserDetailsService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("user")
class UserController(
    private val userRepo: UserRepository,
    private val authorityRepo: AuthorityRepository,
    private val encoder: BCryptPasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: SecurityUserDetailsService,
    private val jwtUtil: JwtUtil
) {

  @PostMapping("register")
  fun register(@Valid @RequestBody user: UserDTO): Mono<Void> {
    return Mono.create<Void> {
      user.password = encoder.encode(user.password)
      try {
        val savedUser = userRepo.save(User(username = user.username, password = user.password, email = user.email, firstName = user.firstName, lastName = user.lastName))
        val authority = Authority(user = savedUser)
        authorityRepo.save(authority)
        it.success()
      } catch (e: Exception) {
        it.error(CannotCreateUserException())
      }
    }
  }

  @PostMapping("login")
  fun login(@Valid @RequestBody authenticationRequest: AuthenticationRequest): Mono<AuthenticationResponse> {
    return Mono.create<AuthenticationResponse> {
      try {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(authenticationRequest.username, authenticationRequest.password))
      } catch (e: Exception) {
        return@create it.error(BadCredentialsException())
      }
      val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
          ?: return@create it.error(UserNotFoundException())
      println(userDetails.username)
      it.success(AuthenticationResponse(jwtUtil.generateToken(userDetails)))
    }
  }

  @PostMapping("details")
  fun details(principal: Principal): Mono<String> {
    return Mono.create<String> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (!foundUser.isPresent) {
        return@create it.error(UserNotFoundException())
      }
      val user = foundUser.get()
      it.success("""{"username": "${user.username}", "email": "${user.email}", "firstName": "${user.firstName}", "lastName": "${user.lastName}"}""")
    }
  }

}