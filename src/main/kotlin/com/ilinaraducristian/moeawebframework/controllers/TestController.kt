package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.moea.QueueItemSolver
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test")
class TestController {

  @GetMapping()
  fun testRoute() {

  }

}