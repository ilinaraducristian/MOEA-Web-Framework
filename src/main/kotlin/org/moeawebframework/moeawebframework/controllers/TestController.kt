package org.moeawebframework.moeawebframework.controllers

import kotlinx.coroutines.reactor.mono
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("test")
class TestController(
  private val userRepository: UserRepository
) {

  @Value("\${user.dir}")
  lateinit var location: String

  @GetMapping
  private fun testEndpoint() = mono {
    println(location)
    return@mono "It works"
  }
// spring-data-r2dbc:1.1.1.RELEASE
}