package org.moeawebframework.moeawebframework.services

import org.junit.jupiter.api.*
import org.moeawebframework.moeawebframework.dao.ProblemUserDAO
import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserServiceTest {

  @Autowired
  lateinit var userDAO: UserDAO

  @Autowired
  lateinit var problemUserDAO: ProblemUserDAO

  @Autowired
  lateinit var userService: UserService

  //  @Test
  @Order(1)
  fun createMoeawebframeworkuser() {
    val moeawebframeworkuser = User()
    moeawebframeworkuser.username = "moeawebframework"
    moeawebframeworkuser.password = "moeawebframework"
    moeawebframeworkuser.firstName = "moeawebframework"
    moeawebframeworkuser.lastName = "moeawebframework"
    moeawebframeworkuser.email = "moeawebframework@email.com"
    userDAO.save(moeawebframeworkuser).map {
      Assertions.assertEquals("moeawebframework", it.username)
    }.block()
  }

  //  @Test
//  @Order(2)
//  fun uploadMoeawebframeworkuserProblems() {
//    val moeawebframeworkuser = userDao.getByUsername("moeawebframework").block()!!
//    val moeawebframeworkuserId = moeawebframeworkuser.id!!
//    val problemUser1 = userService.uploadProblem(moeawebframeworkuserId, "Problem1 name", File("./.gitignore")).block()!!
//    val problemUser2 = userService.uploadProblem(moeawebframeworkuserId, "Problem2 name", File("./build.gradle.kts")).block()!!
//    val problemUser3 = userService.uploadProblem(moeawebframeworkuserId, "Problem3 name", File("./HELP.md")).block()!!
//
//    assertAll(
//        { Assertions.assertEquals(moeawebframeworkuserId, problemUser1.userId) },
//        { Assertions.assertEquals(moeawebframeworkuserId, problemUser2.userId) },
//        { Assertions.assertEquals(moeawebframeworkuserId, problemUser3.userId) }
//    )
//  }

  //  @Test
  @Order(3)
  fun signup() {
    val user = User()
    user.username = "foobar"
    user.password = "foobar"
    user.firstName = "Foo"
    user.lastName = "Bar"
    user.email = "foo@bar.com"
    var savedUser: User? = null
    val problemsUser = userService.signup(user)
        .flatMapMany {
          savedUser = it
          problemUserDAO.getByUserId(it.id!!)
        }.collectList().block()
    assertAll(
        { Assertions.assertEquals(savedUser?.id, problemsUser?.get(0)?.userId) },
        { Assertions.assertEquals(savedUser?.id, problemsUser?.get(1)?.userId) },
        { Assertions.assertEquals(savedUser?.id, problemsUser?.get(2)?.userId) }
    )
  }

}