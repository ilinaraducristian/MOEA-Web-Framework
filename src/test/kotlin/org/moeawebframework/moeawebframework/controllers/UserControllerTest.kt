package org.moeawebframework.moeawebframework.controllers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserControllerTest {

  @Test
  fun signup() {
    val signupInfoDTO = SignupInfoDTO()
    signupInfoDTO.username = "foo"
    signupInfoDTO.password = "bar"
    signupInfoDTO.email = "foo@bar.com"
    signupInfoDTO.firstName = "Foo"

    val response: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/signup")
        .bodyValue(signupInfoDTO)
        .exchange().block()
        ?: return assert(false)

//    println(response.bodyToMono(String::class.java).block())
    Assertions.assertEquals(HttpStatus.OK, response.statusCode())
  }

  @Test
  fun login() {
    val userCredentialsDTO = UserCredentialsDTO()
    userCredentialsDTO.username = "foo"
    userCredentialsDTO.password = "bar"
    val response: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/login")
//        .header("Authorization", "Something")
        .bodyValue(userCredentialsDTO)
        .exchange().block()
        ?: return assert(false)

    println(response.statusCode())
    println(response.bodyToMono(String::class.java).block())
    Assertions.assertEquals(HttpStatus.OK, response.statusCode())
  }

}