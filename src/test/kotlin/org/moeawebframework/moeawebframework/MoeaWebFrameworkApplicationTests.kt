package org.moeawebframework.moeawebframework

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MoeaWebFrameworkApplicationTests {

  @Test
  fun test() {
    val clientResponse = WebClient.create("http://localhost:8080/test").get().exchange().block()
    println("statusCode: ${clientResponse?.statusCode()}")
    println("body: ${clientResponse?.bodyToMono(String::class.java)?.block()}")
  }

}

