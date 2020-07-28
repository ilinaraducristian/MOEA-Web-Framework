package org.moeawebframework.moeawebframework

import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.repositories.ProblemRepository
import org.moeawebframework.moeawebframework.repositories.ProblemUserRepository
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.moeawebframework.moeawebframework.services.UserService
import org.moeawebframework.moeawebframework.services.UserServiceTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MoeaWebFrameworkApplicationTests{

  @Autowired
  lateinit var problemRepository: ProblemRepository

  @Autowired
  lateinit var problemUserRepository: ProblemUserRepository

  @Autowired
  lateinit var userService: UserService

  @Autowired
  lateinit var userRepository: UserRepository

//  @Test
  fun userServiceTest() {

  }

}
