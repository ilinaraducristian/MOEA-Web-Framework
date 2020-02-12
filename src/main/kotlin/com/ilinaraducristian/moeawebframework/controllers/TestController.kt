package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("test")
class TestController(
    val userRepo: UserRepository,
    val problemRepo: ProblemRepository,
    val encoder: BCryptPasswordEncoder
) {

  private val logger = LoggerFactory.getLogger(TestController::class.java)

}