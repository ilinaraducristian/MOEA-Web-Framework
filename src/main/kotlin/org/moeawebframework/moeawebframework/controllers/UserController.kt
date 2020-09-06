package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.exceptions.UserExistsException
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@RestController
@RequestMapping("user")
class UserController(
    private val userService: UserService,
    private val userDAO: UserDAO
) {

  init {
    Thread {
//      userService.signup(User(username = "Marian")).block()
      Thread.sleep(2000)
      println(userDAO.getByUsername("username").block())
    }//.start()
  }

  @PostMapping("signup")
  fun signup(authentication: Authentication): Mono<Unit> {
    val principal = authentication.principal as Jwt
    val username = principal.claims["preferred_username"] as String
    return userDAO.getByUsername(username)
        .flatMap { Mono.error<User>(RuntimeException(UserExistsException)) }
        .switchIfEmpty {
          val user = User(username = username)
          userService.signup(user)
        }
        .map {}
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