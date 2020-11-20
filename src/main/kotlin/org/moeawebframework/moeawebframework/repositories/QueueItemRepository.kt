package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.QueueItem
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface QueueItemRepository : R2dbcRepository<QueueItem, Long> {

  suspend fun findByRabbitIdAndUserEntityId(rabbitId: String, userEntityId: String): QueueItem?

}