package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.services.ProblemSolverService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test")
class TestController(
    private val problemSolverService: ProblemSolverService,
    private val userRepository: UserRepository
) {

  @GetMapping("two")
  fun anotherRoute() {
    throw RuntimeException(UserNotFoundException)
  }

  @GetMapping
  fun testRoute() {
    println(userRepository.count())
  }

}