package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.JwtUtil
import com.ilinaraducristian.moeawebframework.configurations.algorithms
import com.ilinaraducristian.moeawebframework.configurations.problems
import com.ilinaraducristian.moeawebframework.dto.AuthenticationRequestDTO
import com.ilinaraducristian.moeawebframework.dto.AuthenticationResponseDTO
import com.ilinaraducristian.moeawebframework.entities.Authority
import com.ilinaraducristian.moeawebframework.entities.User
import com.ilinaraducristian.moeawebframework.exceptions.BadCredentialsException
import com.ilinaraducristian.moeawebframework.exceptions.CannotCreateUserException
import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
import com.ilinaraducristian.moeawebframework.repositories.AuthorityRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
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
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: SecurityUserDetailsService,
    private val jwtUtil: JwtUtil
) {

  @PostMapping("register")
//  @Resp
  fun register(@Valid @RequestBody user: User): Mono<Unit> {
    return mono {
      user.password = encoder.encode(user.password)
      user.problems = problems
      user.algorithms = algorithms
      try {
        authorityRepo.save(Authority(user = userRepo.save(user)))
        File("moeaData/${user.username}/problems/references").mkdirs()
        File("moeaData/${user.username}/algorithms/").mkdirs()
        return@mono
      } catch (e: Exception) {
        throw RuntimeException(CannotCreateUserException)
      }
    }
  }

  @PostMapping("login")
  fun login(@Valid @RequestBody authenticationRequestDTO: AuthenticationRequestDTO): Mono<AuthenticationResponseDTO> {
    return Mono.create<AuthenticationResponseDTO> {
      try {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(authenticationRequestDTO.username, authenticationRequestDTO.password))
      } catch (e: Exception) {
        throw RuntimeException(BadCredentialsException)
      }
      val userDetails = userDetailsService.loadUserByUsername(authenticationRequestDTO.username)
          ?: throw RuntimeException(UserNotFoundException)
      val user = userRepo.findByUsername(userDetails.username) ?: throw RuntimeException(UserNotFoundException)
      val authenticationResponse = AuthenticationResponseDTO(user)
      authenticationResponse.jwt = jwtUtil.generateToken(userDetails)
      it.success(authenticationResponse)
    }
  }

}