package org.moeawebframework.moeawebframework.controllers

import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/test")
class TestController {

  @GetMapping
  private fun testEndpoint(@PathVariable id: String): Mono<String> {
    return mono {
      "It works"
    }
  }

}