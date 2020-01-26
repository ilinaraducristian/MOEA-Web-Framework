package com.ilinaraducristian.moeawebframework.controllers

import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import javax.annotation.security.PermitAll


@RestController
@RequestMapping("test")
class TestController {

  private val logger = LoggerFactory.getLogger(TestController::class.java)

  @GetMapping("one")
  fun get1() {
    println("one")
  }

  @GetMapping("two")
  fun get2() {
    println("two")
  }

}