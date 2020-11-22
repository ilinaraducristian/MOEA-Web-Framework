package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.moeawebframework.moeawebframework.entities.Algorithm
import org.moeawebframework.moeawebframework.entities.Problem
import org.moeawebframework.moeawebframework.entities.ReferenceSet
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestConfig::class)
class UserServiceTests {

  @Autowired
  lateinit var userService: UserService

  @Test
  fun `should add, return and delete a queue item for this user`() = runBlocking {
    val queueItemDTO = QueueItemDTO(
        "Queue item name",
        15000,
        10,
        "ce63b31c3fb6519d1c71f0b5de2979fb",
        "45b19eb42c0f9288927d5f7f3e4ecb82",
        "c835346a50b0d2335483bb0d6e439df0"
    )
    val rabbitId = userService.addQueueItem("cdd36e48-f1c5-474e-abc3-ac7a17909878", queueItemDTO)
    val queueItem = userService.getQueueItem("cdd36e48-f1c5-474e-abc3-ac7a17909878", rabbitId)

    userService.deleteQueueItem("cdd36e48-f1c5-474e-abc3-ac7a17909878", queueItem?.rabbitId!!)
  }

  @Test
  fun `should return this user's data`() = runBlocking {
    val userData = userService.getUserData("cdd36e48-f1c5-474e-abc3-ac7a17909878")
    val algorithms: List<Algorithm>? = userData["algorithms"] as List<Algorithm>
    val problems: List<Problem>? = userData["problems"] as List<Problem>
    val referenceSets: List<ReferenceSet>? = userData["referenceSets"] as List<ReferenceSet>
    assertNotNull(algorithms)
    assertNotNull(problems)
    assertNotNull(referenceSets)
    assertNotEquals(0, algorithms?.size)
    assertNotEquals(0, problems?.size)
    assertNotEquals(0, referenceSets?.size)
  }

  @Test
  fun `should start processing`() {
    assertDoesNotThrow {
      runBlocking {
        userService.startProcessing("cdd36e48-f1c5-474e-abc3-ac7a17909878", "8896b0d0-dbf5-40db-b982-b2c4a7918368")
      }
    }
  }

}


