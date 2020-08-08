package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("user")
class UserController(
    private val userService: UserService,
    private val userDAO: UserDAO,
    private val encoder: BCryptPasswordEncoder
) {

  @PostMapping("signup")
  fun signup(@RequestBody password: String, authentication: Authentication): Mono<Void> {
    val principal = authentication.principal as Jwt
    val user = User()
    user.username = principal.claims["preferred_username"] as String
    user.password = encoder.encode(password)
    user.email = principal.claims["email"] as String
    user.firstName = principal.claims["given_name"] as String
    user.lastName = principal.claims["family_name"] as String
    return userService.signup(user).flatMap { Mono.empty<Void>() }
  }

  @PostMapping("login")
  fun login(userCredentialsDTO: UserCredentialsDTO, authentication: Authentication?): Mono<RegisteredUserDTO> {
    return if (authentication == null)
      userService.login(userCredentialsDTO)
    else
      userService.oauth2Login((authentication.principal as Jwt).claims["preferred_username"] as String)
  }

//  @PatchMapping("update")
//  fun update(@RequestBody jsonPatch: JsonPatch) {
//    val patched: JsonNode = patch.apply(objectMapper.convertValue(targetCustomer, JsonNode::class.java))
//    return objectMapper.treeToValue(patched, Customer::class.java)
//  }

}