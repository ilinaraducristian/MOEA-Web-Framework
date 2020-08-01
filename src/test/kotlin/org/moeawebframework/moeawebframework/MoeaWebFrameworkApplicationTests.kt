package org.moeawebframework.moeawebframework

import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.repositories.ProblemRepository
import org.moeawebframework.moeawebframework.repositories.ProblemUserRepository
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.moeawebframework.moeawebframework.services.UserService
import org.moeawebframework.moeawebframework.services.UserServiceTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import java.io.File
import java.net.URI


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
  fun someTest() {
  val uri = URI("http://localhost:8180/auth/realms/Moea-Web-Framework")
  println(uri.host)
  }

}
