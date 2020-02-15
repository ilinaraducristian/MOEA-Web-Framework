package com.ilinaraducristian.moeawebframework.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.JwtUtil
import com.ilinaraducristian.moeawebframework.dto.QueueItemDTO
import com.ilinaraducristian.moeawebframework.dto.UserDTO
import com.ilinaraducristian.moeawebframework.entities.Authority
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.entities.User
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.repositories.*
import com.ilinaraducristian.moeawebframework.security.AuthenticationRequest
import com.ilinaraducristian.moeawebframework.security.AuthenticationResponse
import com.ilinaraducristian.moeawebframework.security.SecurityUserDetailsService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("user")
class UserController(
    private val userRepo: UserRepository,
    private val problemRepo: ProblemRepository,
    private val algorithmRepo: AlgorithmRepository,
    private val authorityRepo: AuthorityRepository,
    private val encoder: BCryptPasswordEncoder,
    private val jsonConverter: ObjectMapper,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: SecurityUserDetailsService,
    private val jwtUtil: JwtUtil
) {

  @PostMapping("register")
  fun register(@Valid @RequestBody userDTO: UserDTO): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername("admin")
      if(foundUser.isEmpty) {
        return@create it.error(InternalErrorException())
      }
      val admin = foundUser.get()
      val user = User()
      user.username = userDTO.username
      user.password = encoder.encode(userDTO.password)
      user.email = userDTO.email
      user.firstName = userDTO.firstName
      user.lastName = userDTO.lastName
      val problems = problemRepo.findByUsers(admin)
      problems.forEach { problem ->
        user.problems.add(problem)
        problem.users.add(user)
      }
      val algorithms = algorithmRepo.findByUsers(admin)
      algorithms.forEach { algorithm ->
        user.algorithms.add(algorithm)
        algorithm.users.add(user)
      }
      try {
        authorityRepo.save(Authority(user = userRepo.save(user)))
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
      val user = userRepo.findByUsername(userDetails.username).get()
      var authenticationResponse = AuthenticationResponse()
      authenticationResponse.username = user.username
      authenticationResponse.email = user.email
      authenticationResponse.firstName = user.firstName
      authenticationResponse.lastName = user.lastName
      authenticationResponse.problems = problemRepo.findByUsers(user).map {problem -> problem.name}
      authenticationResponse.algorithms = algorithmRepo.findByUsers(user).map {algorithm -> algorithm.name}
      authenticationResponse.queue = user.queue.map {
        it
      }.toMutableList()
      authenticationResponse.jwt = jwtUtil.generateToken(userDetails)
      it.success(authenticationResponse)
    }
  }

  @PostMapping("details")
  fun details(principal: Principal): Mono<String> {
    return Mono.create<String> {
      userRepo.findByUsername(principal.name).ifPresentOrElse({ user ->
        it.success("""{"username": "${user.username}", "email": "${user.email}", "firstName": "${user.firstName}", "lastName": "${user.lastName}"}""")
      }, {
        it.error(UserNotFoundException())
      })
    }
  }

}