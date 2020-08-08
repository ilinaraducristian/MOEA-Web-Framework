package org.moeawebframework.moeawebframework.controllers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.moeawebframework.moeawebframework.MoeaWebFrameworkApplicationTests
import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.entities.Process
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserQueueControllerTest {

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

    val body = clientResponse.bodyToMono(MoeaWebFrameworkApplicationTests.AccessDTO::class.java).block()
    return body?.access_token!!
  }

  //  @Test
  fun addProcess() {
    val newProcess = Process()


    val clientResponse: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/login")
        .exchange().block()
        ?: return assert(false)

    val statusCode = clientResponse.statusCode()
    val body = clientResponse.bodyToMono(RegisteredUserDTO::class.java).block()

    assertAll(
        { Assertions.assertEquals(HttpStatus.OK, statusCode) },
        { Assertions.assertEquals("foo", body?.username) },
        { Assertions.assertEquals("foo@bar.com", body?.email) },
        { Assertions.assertEquals("Foo", body?.firstName) },
        { Assertions.assertEquals(null, body?.lastName) }

    )
  }

  @Test
  fun process() {
    val token = fetchToken()
    val clientResponse: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/queue/process/0abdf521-8500-47da-9321-2c164e078349")
        .header("Authorization", """Bearer $token""")
        .exchange().block()
        ?: return assert(false)
    println(clientResponse.bodyToMono(String::class.java).block())
  }

}