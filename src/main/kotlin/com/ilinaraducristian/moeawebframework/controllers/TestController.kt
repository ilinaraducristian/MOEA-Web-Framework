package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.entities.ProblemSolver
import com.ilinaraducristian.moeawebframework.entities.User
import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
import com.ilinaraducristian.moeawebframework.services.ProblemSolverService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test")
class TestController(
    private val problemSolverService: ProblemSolverService
) {

  @GetMapping("two")
  fun anotherRoute() {
    throw RuntimeException(UserNotFoundException)
  }

  @GetMapping
  fun testRoute() {
    val problemSolver = ProblemSolver()
    problemSolver.name = "Asd"
    problemSolver.problem = "Belegundu"
    problemSolver.algorithm = "AcoR"
    problemSolver.numberOfSeeds = 10
    problemSolver.numberOfEvaluations = 10000
    problemSolver.user = User(username = "user")
    problemSolver.rabbitId = "plsasdl"
    problemSolverService.solveProblemSolver(problemSolver)
  }

}