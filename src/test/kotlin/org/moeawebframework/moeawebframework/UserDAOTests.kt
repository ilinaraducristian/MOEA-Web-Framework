package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.entities.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
class UserDAOTests {

  @Autowired
  lateinit var userDAO: UserDAO

  companion object {
    val LOGGER = LoggerFactory.getLogger(UserDAOTests::class.java)
  }

  @Test
  fun userDAOTest1() {
    runBlocking {
      assertEquals(User("cdd36e48-f1c5-474e-abc3-ac7a17909878"), userDAO.get("cdd36e48-f1c5-474e-abc3-ac7a17909878"))
      LOGGER.info("[ userDAOTest1 ]: should return an existing user ✓")
    }
  }

  @Test
  fun userDAOTest2() {
    runBlocking {
      assertNull(userDAO.get("77182a16-d486-48dc-b556-2e1778cedcdd"))
      LOGGER.info("[ userDAOTest2 ]: should return null ✓")
    }
  }

  @Test
  fun userDAOTest3() {
    assertThrows(Exception::class.java) {
      runBlocking {
        userDAO.get(0)
      }
    }
    LOGGER.info("[ userDAOTest3 ]: should throw exception ✓")
  }

}

