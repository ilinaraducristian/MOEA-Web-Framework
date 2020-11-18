package org.moeawebframework.moeawebframework.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test")
class TestController {

  @GetMapping
  suspend fun test(): Map<String, Array<String>> {
    val ar1 = arrayOf("asd1", "asd2", "asd3")
    val ar2 = arrayOf("asd4", "asd5", "asd6")
    return mapOf(Pair("ar1", ar1), Pair("ar2", ar2))
  }

}