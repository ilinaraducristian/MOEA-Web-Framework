package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.QueueItem
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface QueueItemRepository : R2dbcRepository<QueueItem, Long> {

  suspend fun findByUserEntityId(userEntityId: String): Flux<QueueItem>

  suspend fun findByUserEntityIdAndRabbitId(userEntityId: String, rabbitId: String): QueueItem?


}