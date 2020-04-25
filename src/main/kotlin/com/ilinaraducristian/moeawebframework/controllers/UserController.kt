package com.ilinaraducristian.moeawebframework.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.JwtUtil
import com.ilinaraducristian.moeawebframework.configurations.algorithms
import com.ilinaraducristian.moeawebframework.configurations.problems
import com.ilinaraducristian.moeawebframework.dto.QueueItemResponseDTO
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
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.io.File
import javax.validation.Valid

@RestController
@RequestMapping("user")
class UserController(
    private val userRepo: UserRepository,
    private val authorityRepo: AuthorityRepository,
    private val encoder: BCryptPasswordEncoder,
    private val jsonConverter: ObjectMapper,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: SecurityUserDetailsService,
    private val jwtUtil: JwtUtil
) {

  @PostMapping("register")
  fun register(@Valid @RequestBody userDTO: UserDTO): Mono<Unit> {
    return mono {
      val user = User()
      user.username = userDTO.username
      user.password = encoder.encode(userDTO.password)
      user.email = userDTO.email
      user.firstName = userDTO.firstName
      user.lastName = userDTO.lastName
      user.problems = problems
      user.algorithms = algorithms
      try {
        authorityRepo.save(Authority(user = userRepo.save(user)))
        File("moeaData/${user.username}/problems/references").mkdirs()
        File("moeaData/${user.username}/algorithms/").mkdirs()
        return@mono
      } catch (e: Exception) {
        throw CannotCreateUserException()
      }
    }
  }

  @PostMapping("login")
  fun login(@Valid @RequestBody authenticationRequest: AuthenticationRequest): Mono<AuthenticationResponse> {
    return mono {
      try {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(authenticationRequest.username, authenticationRequest.password))
      } catch (e: Exception) {
        throw BadCredentialsException()
      }
      val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
          ?: throw UserNotFoundException()
      val user = userRepo.findByUsername(userDetails.username) ?: throw UserNotFoundException()
      val authenticationResponse = AuthenticationResponse()
      authenticationResponse.username = user.username
      authenticationResponse.email = user.email
      authenticationResponse.firstName = user.firstName
      authenticationResponse.lastName = user.lastName
      authenticationResponse.problems = user.problems
      authenticationResponse.algorithms = user.algorithms
      authenticationResponse.queue = user.queue.map { queueItem ->
        val queueItemResponseDTO = QueueItemResponseDTO()
        queueItemResponseDTO.name = queueItem.name
        queueItemResponseDTO.numberOfEvaluations = queueItem.numberOfEvaluations
        queueItemResponseDTO.numberOfSeeds = queueItem.numberOfSeeds
        queueItemResponseDTO.status = queueItem.status
        queueItemResponseDTO.rabbitId = queueItem.rabbitId
        queueItemResponseDTO.results = queueItem.results
        queueItemResponseDTO.problem = queueItem.problem
        queueItemResponseDTO.algorithm = queueItem.algorithm
        return@map queueItemResponseDTO
      }.toList()
      authenticationResponse.jwt = jwtUtil.generateToken(userDetails)
      authenticationResponse
    }
  }

//  @GetMapping
//  fun details(principal: Principal): Mono<String> {
//    return Mono.create<String> {
//      userRepo.findByUsername(principal.name).ifPresentOrElse({ user ->
//        it.success("""{"username": "${user.username}", "email": "${user.email}", "firstName": "${user.firstName}", "lastName": "${user.lastName}"}""")
//      }, {
//        it.error(UserNotFoundException())
//      })
//    }
//  }

}