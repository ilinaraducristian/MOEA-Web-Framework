package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("user")
class UserController(
    private val userService: UserService
) {

  @PostMapping("signup")
  fun signup(authentication: Authentication): Mono<Void> {
    val principal = authentication.principal as Jwt
    val user = User()
    user.username = principal.claims["preferred_username"] as String
    user.password = principal.claims["preferred_username"] as String
    user.email = principal.claims["email"] as String
    user.firstName = principal.claims["given_name"] as String
    user.lastName = principal.claims["family_name"] as String
    return userService.signup(user).flatMap { Mono.empty<Void>() }
  }

//  @PatchMapping("update")
//  fun update(@RequestBody jsonPatch: JsonPatch) {
//    val patched: JsonNode = patch.apply(objectMapper.convertValue(targetCustomer, JsonNode::class.java))
//    return objectMapper.treeToValue(patched, Customer::class.java)
//  }

}