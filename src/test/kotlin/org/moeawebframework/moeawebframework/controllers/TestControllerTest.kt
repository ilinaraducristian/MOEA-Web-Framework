package org.moeawebframework.moeawebframework.controllers

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TestControllerTest {

//  @Test
  fun testEndpoint() {
    val response: ClientResponse = WebClient.create("http://localhost:8080").get()
    .uri("/test")
        .exchange().block()
        ?: return assert(false)

    println(response.bodyToMono(String::class.java).block())
  }

}