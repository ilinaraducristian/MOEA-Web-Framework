package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient


@RestController
@RequestMapping("test")
class TestController(
    private val userRepository: UserRepository
) {
  fun fetchToken(): String {
    val clientResponse: ClientResponse = WebClient.create("http://localhost:8180").post()
        .uri("/auth/realms/MOEA-Web-Framework/protocol/openid-connect/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters
            .fromFormData("username", "foobar")
            .with("password", "foobar")
            .with("client_id", "obtain-token")
            .with("grant_type", "password"))
        .exchange().block()
        ?: throw RuntimeException()

    val body = clientResponse.bodyToMono(AccessDTO::class.java).block()
    return body?.access_token!!
  }

  @GetMapping
  fun testEnd() {
  val token = fetchToken()
    val clientResponse: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/queue/process/0abdf521-8500-47da-9321-2c164e078349")
        .header("Authorization", """Bearer $token""")
        .exchange().block()
        ?: return assert(false)
    println(clientResponse.bodyToMono(String::class.java).block())

  }

  class AccessDTO {
    val access_token: String? = null
  }

}