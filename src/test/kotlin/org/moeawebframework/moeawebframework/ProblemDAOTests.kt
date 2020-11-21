package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dao.ProblemDAO
import org.moeawebframework.moeawebframework.entities.Problem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ProblemDAOTests {

  @Autowired
  lateinit var problemDAO: ProblemDAO

  @Test
  fun `should add, return and delete an problem`() = runBlocking {
    val newProblem = Problem(null, "ProblemName", "5ec6f4e0d2c295f401045b54f1b893d5")
    val problem = problemDAO.save(newProblem)
    assertEquals(problem, problemDAO.get(problem?.id!!))
    problemDAO.delete(problem)

  }

  @Test
  fun `should return null if non existent`() = runBlocking {
    assertNull(problemDAO.get(100))
  }

  @Test
  fun `should return a list with existing problems that belong to this user`() = runBlocking {
    assertNotEquals(0, problemDAO.getByUserEntityId("cdd36e48-f1c5-474e-abc3-ac7a17909878").size)
  }

  @Test
  fun `should return an empty list for an existing user with no problems`() = runBlocking {
    assertEquals(0, problemDAO.getByUserEntityId("cdd36e48-f1c5-474e-abc3-ac7a17909879").size)
  }

  @Test
  fun `should return an empty list for a non existing user`() = runBlocking {
    assertEquals(0, problemDAO.getByUserEntityId("371ac96d-a0b0-4bb0-901d-46cecc31ce1b").size)
  }

  @Test
  fun `should return an problem that belogs to this user by MD5`() = runBlocking {
    assertNotNull(problemDAO.getByUserEntityIdAndMD5("cdd36e48-f1c5-474e-abc3-ac7a17909878", "45b19eb42c0f9288927d5f7f3e4ecb82"))
  }

}

