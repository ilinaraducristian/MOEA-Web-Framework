package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dto.AccessTokenDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.services.HttpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MoeaWebFrameworkApplicationTests {

  @Autowired
  lateinit var httpService: HttpService

//  @Test
  fun test() {
    val clientResponse1 = WebClient.create("http://localhost:8180/auth/realms/MOEA-Web-Framework/protocol/openid-connect/token")
        .post()
        .body(BodyInserters.fromFormData("client_id", "admin-cli")
            .with("grant_type", "client_credentials")
            .with("client_secret", "e622400c-01d7-4cd3-9409-1b5e569170a2")
        )
        .exchange()
        .block()!!
    println("Login admin status code: ${clientResponse1.statusCode()}")
    val accessTokenDTO = clientResponse1.bodyToMono(AccessTokenDTO::class.java).block()!!
    val clientResponse2 = WebClient.create("http://localhost:8180/auth/admin/realms/MOEA-Web-Framework/users")
        .post()
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer ${accessTokenDTO.access_token}")
        .bodyValue("""{"enabled":true,"attributes":{},"username":"marian","emailVerified":"","email":"use3r@email.com","firstName":"User","lastName":"Name"}""")
        .exchange()
        .block()!!
    println("Create user status code: ${clientResponse2.statusCode()}")
    val response = clientResponse2.bodyToMono(String::class.java).block()
    println(response)
  }

//  @Test
  fun refresh_token_test() {
    val clientResponse1 = WebClient.create("http://localhost:8180/auth/realms/MOEA-Web-Framework/protocol/openid-connect/token")
        .post()
        .body(BodyInserters.fromFormData("client_id", "admin-cli")
            .with("grant_type", "client_credentials")
            .with("client_secret", "e622400c-01d7-4cd3-9409-1b5e569170a2")
        )
        .exchange()
        .block()!!
    println("Login admin status code: ${clientResponse1.statusCode()}")
    val accessTokenDTO = clientResponse1.bodyToMono(AccessTokenDTO::class.java).block()!!

    val clientResponse2 = WebClient.create("http://localhost:8180/auth/realms/MOEA-Web-Framework/protocol/openid-connect/token")
        .post()
        .body(BodyInserters.fromFormData("client_id", "admin-cli")
            .with("grant_type", "refresh_token")
            .with("refresh_token", accessTokenDTO.refresh_token)
            .with("client_secret", "e622400c-01d7-4cd3-9409-1b5e569170a2")
        )
        .exchange()
        .block()!!
    println("Login admin status code: ${clientResponse2.statusCode()}")
    println(clientResponse2.bodyToMono(String::class.java).block()!!)
  }

  @Test
  fun login_user_test() {
    val userCredentialsDTO = UserCredentialsDTO(username = "user", password = "password")
    runBlocking {
      val clientResponse = httpService.keycloakLogin(userCredentialsDTO)
      println(clientResponse.awaitBody<String>())
    }
  }

}
