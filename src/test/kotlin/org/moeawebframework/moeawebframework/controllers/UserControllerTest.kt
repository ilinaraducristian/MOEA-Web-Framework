package org.moeawebframework.moeawebframework.controllers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import java.io.File


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserControllerTest {

  fun fetchTokenFromFirstSource(): String {
    val clientResponse: ClientResponse = WebClient.create("http://localhost:8180").post()
        .uri("/auth/realms/MOEA-Web-Framework/protocol/openid-connect/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData("username", "foobar").with("password", "foobar").with("client_id", "obtain-token").with("grant_type", "password"))
        .exchange().block()
        ?: throw RuntimeException()

    val body = clientResponse.bodyToMono(AccessDTO::class.java).block()
    return body?.access_token!!
  }

  fun fetchTokenFromSecondSource(): String {
    val clientResponse: ClientResponse = WebClient.create("http://localhost:8280").post()
        .uri("/auth/realms/MOEA-Web-Framework/protocol/openid-connect/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData("username", "foobar").with("password", "foobar").with("client_id", "obtain-token").with("grant_type", "password"))
        .exchange().block()
        ?: throw RuntimeException()

    val body = clientResponse.bodyToMono(AccessDTO::class.java).block()
    return body?.access_token!!
  }

  //  @Test
  fun smallTestFirstSource() {
    val token = fetchTokenFromFirstSource()
    val clientResponse: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/signup")
        .header("Authorization", """Bearer $token""")
        .exchange().block()
        ?: return assert(false)

    val body = clientResponse.bodyToMono(AccessDTO::class.java).block()
    println(clientResponse.statusCode())
    println(body)
  }

  //  @Test
  fun smallTestSecondSource() {
    val token = fetchTokenFromSecondSource()
    val clientResponse: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/signup")
        .header("Authorization", """Bearer $token""")
        .exchange().block()
        ?: return assert(false)

    val body = clientResponse.bodyToMono(AccessDTO::class.java).block()
    println(clientResponse.statusCode())
    println(body)
  }

  //  @Test
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

    Assertions.assertEquals(HttpStatus.OK, response.statusCode())
  }

  //  @Test
  fun login() {
    val userCredentialsDTO = UserCredentialsDTO()
    userCredentialsDTO.username = "foobar"
    userCredentialsDTO.password = "foobar"

    val clientResponse: ClientResponse = WebClient.create("http://localhost:8180").post()
        .uri("/auth/realms/Moea-Web-Framework/protocol/openid-connect/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData("username", "foobar").with("password", "foobar").with("client_id", "obtain-token").with("grant_type", "password"))
        .exchange().block()
        ?: return assert(false)

    val statusCode = clientResponse.statusCode()
    val body = clientResponse.bodyToMono(AccessDTO::class.java).block()

    println(statusCode)
    println(body?.access_token)
  }

  //  @Test
  fun smallTest() {
    val userCredentialsDTO = UserCredentialsDTO()
    userCredentialsDTO.username = "foobar"
    userCredentialsDTO.password = "foobar"

    val clientResponse: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/user/login")
        .header("Authorization", """Bearer eyeye.w8uwq89eqw89e.asjkdaskldjas""")
        .exchange().block()
        ?: return assert(false)

    val statusCode = clientResponse.statusCode()
    val body = clientResponse.bodyToMono(AccessDTO::class.java).block()

    println(statusCode)
    println(body)
  }

  class AccessDTO {
    val access_token: String? = null
  }

}