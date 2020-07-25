package org.moeawebframework.moeawebframework.controllers

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("user")
class UserController {

  @PostMapping
  fun login(@RequestBody user: UserCredentials) {

  }

}