package com.ilinaraducristian.moeawebframework.controllers

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class TestController {

  private val logger = LoggerFactory.getLogger(TestController::class.java)

  @GetMapping("testEndpoint")
  fun get() {
//    val qualityIndicators = QualityIndicators(Accumulator(), 0)
//    qualityIndicators.Contribution.add(3.5)
//    return qualityIndicators
  }

}