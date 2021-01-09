package org.moeawebframework.moeawebframework.controllers

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/asd")
@CrossOrigin
class TestController {

  @GetMapping
  fun testEndpoint(authentication: Authentication): String {
    val principal = authentication.principal as Jwt
    println(principal.claims)
    return "ALL OK"
  }

}