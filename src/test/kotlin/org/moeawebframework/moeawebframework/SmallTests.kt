package org.moeawebframework.moeawebframework

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SmallTests {

//  val webTestClient = WebClient.create("http://localhost:8080")
//
//  @Test
//  fun tests() {
//    val response: ClientResponse? = webTestClient.put()
//        .uri("/asd")
//        .header("Origin", "http://any-origin.com")
//        .exchangeToMono {
//          println(it.headers())
//        }
//
//    response.expectHeader()
//        .valueEquals("Access-Control-Allow-Origin", "*")
//  }

}