package org.moeawebframework.moeawebframework

import org.moeawebframework.moeawebframework.repositories.ProblemRepository
import org.moeawebframework.moeawebframework.repositories.ProblemUserRepository
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import java.io.File
import javax.mail.Multipart


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MoeaWebFrameworkApplicationTests {

  @Autowired
  lateinit var problemRepository: ProblemRepository

  @Autowired
  lateinit var problemUserRepository: ProblemUserRepository

  @Autowired
  lateinit var userService: UserService

  @Autowired
  lateinit var userRepository: UserRepository

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

//  @Test
  fun uploadAlgorithm() {
    val token = fetchToken()
    val multipart = MultipartBodyBuilder()
    multipart.part("data", File("/home/reydw/asd").readBytes()).header("Content-Disposition", "form-data; name=data; filename=asd")
    multipart.part("name", "Algorithm nou")
    val clientResponse: ClientResponse = WebClient.create("http://localhost:8080").post()
        .uri("/algorithm")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .header("Authorization", """Bearer $token""")
//        .body(BodyInserters.fromMultipartData(multipart.build()))
                .body(BodyInserters.fromMultipartData(multipart.build()))
        .exchange().block()
        ?: return assert(false)

    val body = clientResponse.bodyToMono(String::class.java).block()
    println(clientResponse.statusCode())
    println(body)
  }

//  @Test
  fun downloadAlgorithm() {
    val token = fetchToken()
    val clientResponse: ClientResponse = WebClient.create("http://localhost:8080").get()
        .uri("""/algorithm/j1tiIUfghLmwW1rcPeGMIupxBmXWvOEbJUNXOapDMG8=""")
        .header("Authorization", """Bearer $token""")
        .exchange().block()
        ?: return assert(false)

    val body = clientResponse.bodyToMono(Multipart::class.java).block()
    println(clientResponse.statusCode())
    println(body)
  }

//  @Test
//  fun signup() {
//    val token = fetchTokenFromFirstSource()
//    val clientResponse: ClientResponse = WebClient.create("http://localhost:8080").post()
//        .uri("/user/signup")
//        .header("Authorization", """Bearer $token""")
//        .bodyValue("foobar")
//        .exchange().block()
//        ?: return assert(false)
//
//    val body = clientResponse.bodyToMono(String::class.java).block()
//    println(clientResponse.statusCode())
//    println(body)
//  }

  class AccessDTO {
    val access_token: String? = null
  }

  class CredentialPassword(
      val password: String
  )


}
