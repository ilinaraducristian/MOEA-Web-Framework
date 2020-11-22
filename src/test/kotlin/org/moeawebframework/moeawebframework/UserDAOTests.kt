package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestConfig::class)
class UserDAOTests {

  @Autowired
  lateinit var userDAO: UserDAO

  @Test
  fun `should return an existing user`() = runBlocking {
    assertEquals(User("cdd36e48-f1c5-474e-abc3-ac7a17909878"), userDAO.get("cdd36e48-f1c5-474e-abc3-ac7a17909878"))
  }

  @Test
  fun `should return null`() = runBlocking {
    assertNull(userDAO.get("77182a16-d486-48dc-b556-2e1778cedcdd"))
  }

}

