package com.ilinaraducristian.moeawebframework.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.Problem
import com.ilinaraducristian.moeawebframework.dto.User
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("test")
class TestController(
    val userRepo: UserRepository,
    val problemRepo: ProblemRepository,
    val encoder: BCryptPasswordEncoder,
    val problemRedisTemplate: ReactiveRedisTemplate<Long, Problem>
) {

  private val logger = LoggerFactory.getLogger(TestController::class.java)


  @GetMapping("one")
  fun get1() {
    val user = User(username = "myUsername", password = encoder.encode("myPassword"), email = "foo@bar.com", firstName = "John")
    userRepo.save(user)
    val problem = Problem(userDefinedName = "Problem1", name = "Belegundu", algorithm = "CMA-ES", status = "waiting", user = user)
    val problem2 = Problem(userDefinedName = "Problem2", name = "Belegundu", algorithm = "CMA-ES", status = "waiting", user = user)
    problemRepo.save(problem)
    problemRepo.save(problem2)
//    problemRepo.findByUserDefinedName("Problem1")
    val problems = problemRepo.findByUserUsername("myUsername")
    println(ObjectMapper().writeValueAsString(problems[0].user.firstName))
    user.firstName = "Doe"
    userRepo.save(user)
    println(ObjectMapper().writeValueAsString(problems[0].user.firstName))
  }

  @GetMapping("two")
  fun get2() {
//    problemRedisTemplate.opsForSet().add(69, Problem(userDefinedName = "Problem1"))
//        .then(problemRedisTemplate.opsForSet().add(69, Problem(userDefinedName = "Problem2")))
//        .then(problemRedisTemplate.opsForSet().add(69, Problem(userDefinedName = "Problem3")))
//        .then(problemRedisTemplate.opsForSet().add(69, Problem(userDefinedName = "Problem4")))
//        .then(problemRedisTemplate.opsForSet().add(69, Problem(userDefinedName = "Problem5"))).subscribe()
    println("Two")
    problemRedisTemplate.opsForSet()
        .members(69)
        .filter { problem ->
          problem.userDefinedName == "Problem5"
        }
        .count().subscribe { size ->
          println(size)
        }
  }

}