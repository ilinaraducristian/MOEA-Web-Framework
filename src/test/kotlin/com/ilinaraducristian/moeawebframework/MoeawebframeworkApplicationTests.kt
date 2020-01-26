package com.ilinaraducristian.moeawebframework

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.Problem
import com.ilinaraducristian.moeawebframework.dto.User
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.annotation.Resource
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class MoeawebframeworkApplicationTests() {

	@Resource
	lateinit var userRepo: UserRepository

	@Test
	fun contextLoads() {
		val user = User(username = "user", email = "marian@foo.bar")
		user.problems.add(Problem(6, "Problem", "asd", "working", null))
		userRepo.save(user)
		println("User: ${ObjectMapper().writeValueAsString(userRepo.findByUsername("user"))}")
	}

}
