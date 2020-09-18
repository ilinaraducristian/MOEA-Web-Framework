package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.dto.AccessTokenDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("user")
class UserController(
    private val userService: UserService,
    private val userDAO: UserDAO
) {

  @PostMapping("signup")
  fun signup(@Valid signupInfo: SignupInfoDTO): Mono<Unit> {
    return userService.signup(signupInfo).map {}
  }

  @PostMapping("login")
  fun login(userCredentialsDTO: UserCredentialsDTO): Mono<AccessTokenDTO> {
    return userService.login(userCredentialsDTO)
  }

  @GetMapping("getAlgorithmsAndProblems")
  fun getAlgorithmsAndProblems(authentication: Authentication): Mono<HashMap<String, List<Any>>> {
    val principal = authentication.principal as Jwt
    return userDAO.getByUsername(principal.claims["preferred_username"] as String)
        .switchIfEmpty(Mono.error(RuntimeException(UserNotFoundException)))
        .flatMap { userService.getAlgorithmsAndProblems(it.id!!) }
  }

//  @PatchMapping("update")
//  fun update(@RequestBody jsonPatch: JsonPatch) {
//    val patched: JsonNode = patch.apply(objectMapper.convertValue(targetCustomer, JsonNode::class.java))
//    return objectMapper.treeToValue(patched, Customer::class.java)
//  }

}