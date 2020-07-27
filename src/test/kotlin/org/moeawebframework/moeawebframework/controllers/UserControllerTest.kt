package org.moeawebframework.moeawebframework.controllers

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserControllerTest {

  @Test
  fun signup() {
    val response: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/signup")
        .header("Authorization", "Something")
        .exchange().block()
        ?: return assert(false)

    println(response.statusCode())
    println(response.bodyToMono(String::class.java).block())
  }

  @Test
  fun login() {
    val response: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/login")
        .header("Authorization", "Something")
        .exchange().block()
        ?: return assert(false)

    println(response.statusCode())
    println(response.bodyToMono(String::class.java).block())
  }

}