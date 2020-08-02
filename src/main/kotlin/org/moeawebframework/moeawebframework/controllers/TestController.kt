package org.moeawebframework.moeawebframework.controllers

import kotlinx.coroutines.reactor.mono
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.server.WebFilterChainProxy
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest


@RestController
@RequestMapping("test")
class TestController(
  private val userRepository: UserRepository
) {

}