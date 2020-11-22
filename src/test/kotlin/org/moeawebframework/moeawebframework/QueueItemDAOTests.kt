package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dao.QueueItemDAO
import org.moeawebframework.moeawebframework.entities.QueueItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestConfig::class)
class QueueItemDAOTests {

  @Autowired
  lateinit var queueItemDAO: QueueItemDAO

  @Test
  fun `should add, return and delete a queue item`() = runBlocking {
    val newQueueItem = QueueItem(
        name = "Queue item name",
        numberOfEvaluations = 15000,
        numberOfSeeds = 10,
        rabbitId = "ebaa9239-c8e7-4140-b149-5c2c19d3e483",
        algorithmMD5 = "ce63b31c3fb6519d1c71f0b5de2979fb",
        problemMD5 = "45b19eb42c0f9288927d5f7f3e4ecb82",
        referenceSetMD5 = "c835346a50b0d2335483bb0d6e439df0",
        userEntityId = "cdd36e48-f1c5-474e-abc3-ac7a17909878")
    val queueItem = queueItemDAO.save(newQueueItem)
    assertEquals(queueItem, queueItemDAO.get(queueItem?.id!!))
    queueItemDAO.delete(queueItem)
  }

  @Test
  fun `should return null if non existent`() = runBlocking {
    assertNull(queueItemDAO.get(100))
  }

  @Test
  fun `should return a list with existing queue items that belong to this user`() = runBlocking {
    assertNotEquals(0, queueItemDAO.getByUserEntityId("cdd36e48-f1c5-474e-abc3-ac7a17909878").size)
  }

  @Test
  fun `should return an empty list for an existing user with no queue items`() = runBlocking {
    assertEquals(0, queueItemDAO.getByUserEntityId("cdd36e48-f1c5-474e-abc3-ac7a17909879").size)
  }

  @Test
  fun `should return an empty list for a non existing user`() = runBlocking {
    assertEquals(0, queueItemDAO.getByUserEntityId("371ac96d-a0b0-4bb0-901d-46cecc31ce1b").size)
  }

  @Test
  fun `should return an queue item that belogs to this user by Rabbit id`() = runBlocking {
    assertNotNull(queueItemDAO.getByUserEntityIdAndRabbitId("cdd36e48-f1c5-474e-abc3-ac7a17909878", "8896b0d0-dbf5-40db-b982-b2c4a7918368"))
  }

}

