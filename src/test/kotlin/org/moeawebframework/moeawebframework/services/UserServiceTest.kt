package org.moeawebframework.moeawebframework.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.moeawebframework.moeawebframework.dao.ProblemUserDAO
import org.moeawebframework.moeawebframework.dao.UserDao
import org.moeawebframework.moeawebframework.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserServiceTest {

  @Autowired
  lateinit var userDao: UserDao

  @Autowired
  lateinit var problemUserDAO: ProblemUserDAO

  @Autowired
  lateinit var userService: UserService

//  @Test
  fun uploadProblemTest() {
    var user = User()
    user.username = "foo"
    user.password = "bar"
    user.email = "foo@bar.com"
    user.firstName = "Foo"
    var user2 = User()
    user2.username = "asd"
    user2.password = "gfdg"
    user2.email = "hgfhgf"
    user2.firstName = "ytyr"
    user = userDao.save(user).block()!!
    user2 = userDao.save(user2).block()!!
    userService.uploadProblem(user, "Problem name", File("/mnt/hdd/fooBar")).block()
    userService.uploadProblem(user2, "Problem name", File("/mnt/hdd/fooBar")).block()
    val problemsUsers = problemUserDAO.getAll().collectList().block()
    assertAll(
        { Assertions.assertEquals(1, problemsUsers?.get(0)?.userId) },
        { Assertions.assertEquals(1, problemsUsers?.get(0)?.problemId) },
        { Assertions.assertEquals(2, problemsUsers?.get(1)?.userId) },
        { Assertions.assertEquals(1, problemsUsers?.get(1)?.problemId) }
    )
  }

}