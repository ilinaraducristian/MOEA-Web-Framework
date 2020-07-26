package org.moeawebframework.moeawebframework.controllers

import kotlinx.coroutines.reactor.mono
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.server.WebFilterChainProxy
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("test")
class TestController(
  private val userRepository: UserRepository
) {

  @GetMapping
  private fun testEndpoint() = mono {
    return@mono "[ TestController ] testEndpoint()"
  }

}