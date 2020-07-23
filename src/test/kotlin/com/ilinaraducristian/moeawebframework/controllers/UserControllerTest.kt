package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.dto.AuthenticationRequestDTO
import com.ilinaraducristian.moeawebframework.entities.User
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class UserControllerTest {

  @Test
  @Order(1)
  fun register() {
    val user = User()
    user.username = "username"
    user.password = "encoded_password"
    user.email = "user@email.com"
    user.firstName = "User Firstname"
    user.lastName = "User Lastname"
    val response: ClientResponse? = WebClient.create("http://localhost:8080").post().uri("/user/register").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .exchange().block()
    assert(response!!.statusCode() == HttpStatus.OK)
  }

  @Test
  fun registerExisting() {
    val user1 = User()
    user1.username = "username"
    user1.password = "encoded_password"
    user1.email = "user1@email.com"
    user1.firstName = "User Firstname"
    user1.lastName = "User Lastname"
    val user2 = User()
    user2.username = "username1"
    user2.password = "encoded_password"
    user2.email = "user@email.com"
    user2.firstName = "User Firstname"
    user2.lastName = "User Lastname"
    val client = WebClient.builder()
        .baseUrl("http://localhost:8080")
        .defaultHeader("Content-Type", "application/json")
        .build().post().uri("/user/register")
    val response1: ClientResponse? = client.bodyValue(user1).exchange().block()
    val response2: ClientResponse? = client.bodyValue(user2).exchange().block()
    assert(response1!!.statusCode() == HttpStatus.CONFLICT &&
        response2!!.statusCode() == HttpStatus.CONFLICT)

  }

  @Test
  @Order(2)
  fun login() {
    val authenticationRequestDTO = AuthenticationRequestDTO()
    authenticationRequestDTO.username = "username"
    authenticationRequestDTO.password = "encoded_password"
    val response: ClientResponse = WebClient.create("http://localhost:8080").post().uri("/user/login").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(authenticationRequestDTO)
        .exchange().block()
        ?: return assert(false)

    Assertions.assertEquals(HttpStatus.OK, response.statusCode())
  }

  @Test
  @Order(3)
  fun badLogin() {
    val authenticationRequestDTO = AuthenticationRequestDTO()
    val client = WebClient.builder()
        .baseUrl("http://localhost:8080")
        .defaultHeader("Content-Type", "application/json")
        .build().post().uri("/user/login")
    // wrong username and password
    authenticationRequestDTO.username = "foo"
    authenticationRequestDTO.password = "bar"
    val response1 = client.bodyValue(authenticationRequestDTO).exchange().block()

    // wrong username
    authenticationRequestDTO.username = "foo"
    authenticationRequestDTO.password = "encoded_password"
    val response2 = client.bodyValue(authenticationRequestDTO).exchange().block()

    // wrong password
    authenticationRequestDTO.username = "username"
    authenticationRequestDTO.password = "bar"
    val response3 = client.bodyValue(authenticationRequestDTO).exchange().block()

    assertAll("RESPONSE",
        { Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response1!!.statusCode()) },
        { Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response2!!.statusCode()) },
        { Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response3!!.statusCode()) }
    )

  }

}