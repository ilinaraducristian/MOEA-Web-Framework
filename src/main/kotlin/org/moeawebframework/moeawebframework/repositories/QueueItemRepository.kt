package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.QueueItem
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface QueueItemRepository : R2dbcRepository<QueueItem, Long> {

  suspend fun findByRabbitIdAndUserId(rabbitId: String, userId: String): QueueItem?

}