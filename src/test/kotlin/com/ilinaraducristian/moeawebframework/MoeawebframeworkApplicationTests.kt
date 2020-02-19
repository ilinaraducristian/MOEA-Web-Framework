package com.ilinaraducristian.moeawebframework

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.UserDTO
import com.ilinaraducristian.moeawebframework.security.AuthenticationRequest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import javax.annotation.Resource
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MoeawebframeworkApplicationTests {

  private val log = LoggerFactory.getLogger(MoeawebframeworkApplicationTests::class.java)
  private val LOCALHOST = "http://localhost:8080"
  private var userJWT: String? = null

  @Resource
  lateinit var testRestTemplate: TestRestTemplate

  @Test
  @Order(1)
  fun createAdmin() {
    val response = testRestTemplate.getForEntity<Void>(LOCALHOST + "/test/admin", Void::class.java)
    assert(response.statusCode == HttpStatus.OK)
  }

  @Test
  @Order(2)
  fun createUser() {
    val userDTO = UserDTO()
    userDTO.email = "user@email.com"
    userDTO.firstName = "User"
    userDTO.password = "user"
    userDTO.username = "user"
    val response = testRestTemplate.postForEntity<Void>(LOCALHOST + "user/register", userDTO, Void::class.java)
    assert(response.statusCode == HttpStatus.OK)
  }

  @Test
  @Order(3)
  fun loginUser() {
    val authenticationRequest = AuthenticationRequest()
    authenticationRequest.username = "user"
    authenticationRequest.password = "user"
    val response = testRestTemplate.postForEntity<LoginResponse>(LOCALHOST + "user/login", authenticationRequest, LoginResponse::class.java)
    userJWT = response.body?.jwt
    assert(response.statusCode == HttpStatus.OK)
  }

  class LoginResponse {
    val username: String = ""
    val email: String = ""
    val firstName: String = ""
    val lastName: String? = null
    val jwt: String = ""
    val problems: Array<String> = arrayOf()
    val algorithms: Array<String> = arrayOf()
    val queue: Array<String> = arrayOf()
  }

}
