package org.moeawebframework.moeawebframework.controllers

import com.github.fge.jsonpatch.JsonPatch
import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("user")
class UserController(
    private val userService: UserService
) {

  @PostMapping("signup")
  fun signup(authentication: Authentication) {
//    println(Arrays.toString(Thread.currentThread().getStackTrace()).replace( ',', '\n' ))
    println("""details: ${authentication.details}""")
    val principal = authentication.principal as Jwt
    principal.claims.forEach {
      println("""[ ${it.key} ]: ${it.value}""")
    }
  }

  @PostMapping("login")
  fun login(@RequestBody @Valid userCredentials: UserCredentialsDTO): Mono<RegisteredUserDTO> {
    return userService.login(userCredentials)
  }

//  @PatchMapping("update")
//  fun update(@RequestBody jsonPatch: JsonPatch) {
//    val patched: JsonNode = patch.apply(objectMapper.convertValue(targetCustomer, JsonNode::class.java))
//    return objectMapper.treeToValue(patched, Customer::class.java)
//  }

}