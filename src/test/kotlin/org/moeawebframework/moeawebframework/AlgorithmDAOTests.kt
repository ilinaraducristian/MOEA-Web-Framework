package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dao.AlgorithmDAO
import org.moeawebframework.moeawebframework.entities.Algorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class AlgorithmDAOTests {

  @Autowired
  lateinit var algorithmDAO: AlgorithmDAO

  @Test
  fun `should add, return and delete an algorithm`() = runBlocking {
    val newAlgorithm = Algorithm(null, "AlgorithmName", "9e6ec1be8adc0041c727f77ee1d67c5e")
    val algorithm = algorithmDAO.save(newAlgorithm)
    assertEquals(algorithm, algorithmDAO.get(algorithm?.id!!))
    algorithmDAO.delete(algorithm)

  }

  @Test
  fun `should return null if non existent`() = runBlocking {
    assertNull(algorithmDAO.get(100))
  }

  @Test
  fun `should return a list with existing algorithms that belong to this user`() = runBlocking {
    assertNotEquals(0, algorithmDAO.getByUserEntityId("cdd36e48-f1c5-474e-abc3-ac7a17909878").size)
  }

  @Test
  fun `should return an empty list for an existing user with no algorithms`() = runBlocking {
    assertEquals(0, algorithmDAO.getByUserEntityId("cdd36e48-f1c5-474e-abc3-ac7a17909879").size)
  }

  @Test
  fun `should return an empty list for a non existing user`() = runBlocking {
    assertEquals(0, algorithmDAO.getByUserEntityId("371ac96d-a0b0-4bb0-901d-46cecc31ce1b").size)
  }

  @Test
  fun `should return an algorithm that belogs to this user by MD5`() = runBlocking {
    assertNotNull(algorithmDAO.getByUserEntityIdAndMD5("cdd36e48-f1c5-474e-abc3-ac7a17909878", "ce63b31c3fb6519d1c71f0b5de2979fb"))
  }

}

