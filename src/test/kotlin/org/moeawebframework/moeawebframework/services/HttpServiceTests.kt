package org.moeawebframework.moeawebframework.services

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.moeawebframework.moeawebframework.dto.KeycloakUserDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.awaitBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class HttpServiceTests {

  @Autowired
  lateinit var httpService: HttpService

  //  @Test
  fun create_find_delete_user_keycloak() {
    create_user()
    val id = find_user()
    delete_user(id)
  }

  private fun create_user() {
    val signupInfoDTO = SignupInfoDTO("user", "pass", "user@email.com", "First", "Second")
    runBlocking {
      val clientResponse = httpService.keycloakSignup(signupInfoDTO)
      Assertions.assertEquals(clientResponse.statusCode(), HttpStatus.CREATED)
    }
  }

  private fun find_user(): String {
    return runBlocking {
      val clientResponse = httpService.keycloakFindByEmail("user@email.com")
      Assertions.assertEquals(clientResponse.statusCode(), HttpStatus.OK)
      return@runBlocking clientResponse.awaitBody<Array<KeycloakUserDTO>>()[0].id
    }
  }

  private fun delete_user(id: String) {
    runBlocking {
      val clientResponse = httpService.keycloakDelete(id)
      Assertions.assertEquals(clientResponse.statusCode(), HttpStatus.NO_CONTENT)
    }
  }

}