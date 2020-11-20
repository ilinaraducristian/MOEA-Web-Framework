package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dao.AlgorithmDAO
import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.entities.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
class AlgorithmDAOTests {

  @Autowired
  lateinit var algorithmDAO: AlgorithmDAO

  companion object {
    val LOGGER = LoggerFactory.getLogger(AlgorithmDAOTests::class.java)
  }

  @Test
  fun algorithmDAOTest1() {
    runBlocking {
      println(algorithmDAO.getByUserEntityId("cdd36e48-f1c5-474e-abc3-ac7a17909878"))
      LOGGER.info("[ algorithmDAOTest1 ]: should return an existing algorithm ✓")
    }
  }

}

