package org.moeawebframework.moeawebframework.services

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.moeawebframework.moeawebframework.services.PublicService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PublicServiceTests {

  @Autowired
  lateinit var publicService: PublicService

  @Test
  fun `should add, return and delete queue item`() = runBlocking {
    val queueItemDTO = QueueItemDTO(
        "Queue item name",
        15000,
        10,
        "ce63b31c3fb6519d1c71f0b5de2979fb",
        "45b19eb42c0f9288927d5f7f3e4ecb82",
        "c835346a50b0d2335483bb0d6e439df0"
    )
    val rabbitId = publicService.addQueueItem(queueItemDTO)
    val queueItem = publicService.getQueueItem(rabbitId)
    publicService.deleteQueueItem(queueItem?.rabbitId!!)
  }

}