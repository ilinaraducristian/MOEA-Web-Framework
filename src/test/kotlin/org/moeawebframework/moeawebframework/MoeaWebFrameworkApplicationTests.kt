package org.moeawebframework.moeawebframework

import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MoeaWebFrameworkApplicationTests {

  @Autowired
  lateinit var userRepository: UserRepository

  @Test
  fun saveUser() {
    val user = User()
    user.username = "my username"
    userRepository.save(user).block()
  }

  @Test
  fun findUser() {
    println(userRepository.findById(0).block()!!.username)
  }

}
