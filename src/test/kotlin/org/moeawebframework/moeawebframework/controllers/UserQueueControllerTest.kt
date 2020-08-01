package org.moeawebframework.moeawebframework.controllers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserQueueControllerTest {

//  @Test
  fun testRoute() {
//    val signupInfoDTO = SignupInfoDTO()
//    signupInfoDTO.username = "foo"
//    signupInfoDTO.password = "bar"
//    signupInfoDTO.email = "foo@bar.com"
//    signupInfoDTO.firstName = "Foo"

    val response: ClientResponse = WebClient.create("http://localhost:8080").get()
        .uri("/user/queue")
        .exchange().block()
        ?: return assert(false)

    println(response.bodyToMono(String::class.java).block())

    Assertions.assertEquals(HttpStatus.OK, response.statusCode())
  }

//  @Test
  fun login() {
    val userCredentialsDTO = UserCredentialsDTO()
    userCredentialsDTO.username = "foo"
    userCredentialsDTO.password = "bar"

    val clientResponse: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/login")
        .bodyValue(userCredentialsDTO)
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

//  @Test
  @WithMockUser(username = "user")
  fun anotherTest() {

  }

}