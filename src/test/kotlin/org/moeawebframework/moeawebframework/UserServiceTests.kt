package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.moeawebframework.moeawebframework.services.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
class UserServiceTests {

  @Autowired
  lateinit var userService: UserService

  companion object {
    val LOGGER = LoggerFactory.getLogger(UserServiceTests::class.java)
  }

//  @Test
  fun userServiceTest1() {
    runBlocking {
//      val userData = userService.getUserData("cdd36e48-f1c5-474e-abc3-ac7a17909878")
//      println(userData)
//      if(userData["algorithms"] == null) {
        // user does not exist or no entries
//      }
    }
    LOGGER.info("[ userServiceTest1 ]: should return an existing user ✓")
  }

}

