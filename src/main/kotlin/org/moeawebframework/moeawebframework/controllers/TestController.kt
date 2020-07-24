package org.moeawebframework.moeawebframework.controllers

import kotlinx.coroutines.reactor.mono
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/test")
class TestController(

) {

  @Autowired
  lateinit var userRepository: UserRepository

  @GetMapping
  private fun testEndpoint() = mono {
      return@mono "It works"
  }
// spring-data-r2dbc:1.1.1.RELEASE
}