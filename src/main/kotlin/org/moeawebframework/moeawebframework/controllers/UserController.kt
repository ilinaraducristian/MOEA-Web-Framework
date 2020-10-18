package org.moeawebframework.moeawebframework.controllers

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.dto.KeycloakTokenDTO
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
import javax.validation.Valid

@RestController
@RequestMapping("user")
class UserController(
    private val userService: UserService,
    private val userDAO: UserDAO
) {

  @PostMapping("signup")
  suspend fun signup(@Valid signupInfo: SignupInfoDTO) {
    userService.signup(signupInfo)
  }

  @PostMapping("login")
  suspend fun login(userCredentialsDTO: UserCredentialsDTO): KeycloakTokenDTO {
    return userService.login(userCredentialsDTO)
  }

  @GetMapping("getAlgorithmsAndProblems")
  suspend fun getAlgorithmsAndProblems(authentication: Authentication): HashMap<String, List<Any>> {
    val principal = authentication.principal as Jwt
    val user = userDAO.getByUsername(principal.claims["preferred_username"] as String).awaitFirstOrNull()
        ?: throw RuntimeException(UserNotFoundException)
    return userService.getAlgorithmsAndProblems(user.id!!)
  }

//  @PatchMapping("update")
//  fun update(@RequestBody jsonPatch: JsonPatch) {
//    val patched: JsonNode = patch.apply(objectMapper.convertValue(targetCustomer, JsonNode::class.java))
//    return objectMapper.treeToValue(patched, Customer::class.java)
//  }

}