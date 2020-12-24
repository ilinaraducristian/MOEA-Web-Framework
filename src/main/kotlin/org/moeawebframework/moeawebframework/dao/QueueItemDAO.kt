package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.entities.QueueItem
import org.moeawebframework.moeawebframework.repositories.QueueItemRepository
import org.springframework.stereotype.Repository

@Repository
class QueueItemDAO(
    private val queueItemRepository: QueueItemRepository
) : DAO<QueueItem, Long>(queueItemRepository) {

  suspend fun getAllByUserEntityId(userEntityId: String): List<QueueItem> {
    return queueItemRepository.findAllByUserEntityId(userEntityId).collectList().awaitSingle()
  }

  suspend fun getByUserEntityId(userEntityId: String): List<QueueItem> {
    return queueItemRepository.findByUserEntityId(userEntityId).collectList().awaitSingle()
  }

  suspend fun getByUserEntityIdAndRabbitId(userEntityId: String, rabbitId: String): QueueItem? {
    return queueItemRepository.findByUserEntityIdAndRabbitId(userEntityId, rabbitId)
  }

}