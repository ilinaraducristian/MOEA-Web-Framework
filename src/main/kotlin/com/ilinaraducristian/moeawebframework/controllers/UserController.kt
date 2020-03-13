package com.ilinaraducristian.moeawebframework.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.JwtUtil
import com.ilinaraducristian.moeawebframework.dto.QueueItemResponseDTO
import com.ilinaraducristian.moeawebframework.dto.UserDTO
import com.ilinaraducristian.moeawebframework.entities.Algorithm
import com.ilinaraducristian.moeawebframework.entities.Authority
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.entities.User
import com.ilinaraducristian.moeawebframework.exceptions.BadCredentialsException
import com.ilinaraducristian.moeawebframework.exceptions.CannotCreateUserException
import com.ilinaraducristian.moeawebframework.exceptions.InternalErrorException
import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
import com.ilinaraducristian.moeawebframework.moea.CustomAlgorithmProvider
import com.ilinaraducristian.moeawebframework.moea.CustomProblemProvider
import com.ilinaraducristian.moeawebframework.repositories.AlgorithmRepository
import com.ilinaraducristian.moeawebframework.repositories.AuthorityRepository
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.security.AuthenticationRequest
import com.ilinaraducristian.moeawebframework.security.AuthenticationResponse
import com.ilinaraducristian.moeawebframework.security.SecurityUserDetailsService
import org.moeaframework.core.spi.AlgorithmFactory
import org.moeaframework.core.spi.ProblemFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.io.File
import java.security.Principal
import javax.validation.Valid

val problems = arrayOf("Belegundu", "DTLZ1_2", "DTLZ2_2", "DTLZ3_2", "DTLZ4_2", "DTLZ7_2", "ROT_DTLZ1_2", "ROT_DTLZ2_2", "ROT_DTLZ3_2", "ROT_DTLZ4_2", "ROT_DTLZ7_2", "UF1", "UF2", "UF3", "UF4", "UF5", "UF6", "UF7", "UF8", "UF9", "UF10", "UF11", "UF12", "UF13", "CF1", "CF2", "CF3", "CF4", "CF5", "CF6", "CF7", "CF8", "CF9", "CF10", "LZ1", "LZ2", "LZ3", "LZ4", "LZ5", "LZ6", "LZ7", "LZ8", "LZ9", "WFG1_2", "WFG2_2", "WFG3_2", "WFG4_2", "WFG5_2", "WFG6_2", "WFG7_2", "WFG8_2", "WFG9_2", "ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT5", "ZDT6", "Binh", "Binh2", "Binh3", "Binh4", "Fonseca", "Fonseca2", "Jimenez", "Kita", "Kursawe", "Laumanns", "Lis", "Murata", "Obayashi", "OKA1", "OKA2", "Osyczka", "Osyczka2", "Poloni", "Quagliarella", "Rendon", "Rendon2", "Schaffer", "Schaffer2", "Srinivas", "Tamaki", "Tanaka", "Viennet", "Viennet2", "Viennet3", "Viennet4")
val algorithms = arrayOf("CMA-ES", "NSGAII", "NSGAIII", "GDE3", "eMOEA", "eNSGAII", "MOEAD", "MSOPS", "SPEA2", "PAES", "PESA2", "OMOPSO", "SMPSO", "IBEA", "SMS-EMOA", "VEGA", "DBEA", "RVEA", "RSO")

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
      val guest = userRepo.findByUsername("guest")
      if(guest.isEmpty) return@create it.error(InternalErrorException())
      val problems = problemRepo.findByUsers(guest.get())
      val algorithms = algorithmRepo.findByUsers(guest.get())
      if(problems.isEmpty() || algorithms.isEmpty()) return@create it.error(InternalErrorException())
      val user = User()
      user.username = userDTO.username
      user.password = encoder.encode(userDTO.password)
      user.email = userDTO.email
      user.firstName = userDTO.firstName
      user.lastName = userDTO.lastName
      problems.forEach { problem ->
        user.problems.add(problem)
        problem.users.add(user)
      }
      algorithms.forEach { algorithm ->
        user.algorithms.add(algorithm)
        algorithm.users.add(user)
      }
      try {
        authorityRepo.save(Authority(user = userRepo.save(user)))
        File("moeaData/${user.username}/problems/references").mkdirs()
        File("moeaData/${user.username}/algorithms/").mkdirs()
        ProblemFactory.getInstance().addProvider(CustomProblemProvider(user.username))
        AlgorithmFactory.getInstance().addProvider(CustomAlgorithmProvider(user.username))
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
      authenticationResponse.problems = problemRepo.findByUsers(user).map { problem -> problem.name }
      authenticationResponse.algorithms = algorithmRepo.findByUsers(user).map { algorithm -> algorithm.name }
      authenticationResponse.queue = user.queue.map { queueItem ->
        val queueItemResponseDTO = QueueItemResponseDTO()
        queueItemResponseDTO.name = queueItem.name
        queueItemResponseDTO.numberOfEvaluations = queueItem.numberOfEvaluations
        queueItemResponseDTO.numberOfSeeds = queueItem.numberOfSeeds
        queueItemResponseDTO.status = queueItem.status
        queueItemResponseDTO.rabbitId = queueItem.rabbitId
        queueItemResponseDTO.solverId = queueItem.solverId
        queueItemResponseDTO.results = queueItem.results
        queueItemResponseDTO.problem = queueItem.problem.name
        queueItemResponseDTO.algorithm = queueItem.algorithm.name
        return@map queueItemResponseDTO
      }.toList()
      authenticationResponse.jwt = jwtUtil.generateToken(userDetails)
      it.success(authenticationResponse)
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