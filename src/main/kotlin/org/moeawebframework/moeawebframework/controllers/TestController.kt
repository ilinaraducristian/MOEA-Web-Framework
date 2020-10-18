package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.services.HttpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test")
class TestController {

  @Autowired
  lateinit var httpService: HttpService

  @GetMapping
  suspend fun test(): String {
    httpService.mustErrorFcn()
    return "OK"
  }

}