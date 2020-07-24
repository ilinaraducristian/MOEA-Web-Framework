package org.moeawebframework.moeawebframework.controllers

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
class TestControllerTest {

  @Test
  fun testEndpoint() {
    val client: ClientResponse = WebClient.create("http://localhost:8080")
        .get().uri("/test")
        .exchange().block()
        ?: return assert(false)

    val response = client.bodyToMono(String::class.java).block()
    println(response)
  }

}