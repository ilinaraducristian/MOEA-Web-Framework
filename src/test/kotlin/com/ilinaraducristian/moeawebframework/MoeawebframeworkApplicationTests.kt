package com.ilinaraducristian.moeawebframework

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.Problem
import com.ilinaraducristian.moeawebframework.dto.User
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import javax.annotation.Resource
import javax.transaction.Transactional

@SpringBootTest()
@Transactional
class MoeawebframeworkApplicationTests() {

	@Resource
	lateinit var userRepo: UserRepository
	@Resource
	lateinit var problemRepo: ProblemRepository

	@Test
	fun contextLoads() {
		val user = User(username = "user", email = "marian@foo.bar")
		userRepo.save(user)
		problemRepo.save(Problem(userDefinedName = "Problem1", name = "Belegundu", algorithm = "CMA-ES", status = "working", user = user))
		problemRepo.save(Problem(userDefinedName = "Problem2", name = "Belegundu", algorithm = "CMA-ES", status = "working", user = user))
		println("User: ${ObjectMapper().writeValueAsString(userRepo.findByUsername("user"))}")
	}

}
