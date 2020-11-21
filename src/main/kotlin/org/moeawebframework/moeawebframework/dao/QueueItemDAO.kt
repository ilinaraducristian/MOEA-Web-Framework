package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.QueueItem
import org.moeawebframework.moeawebframework.repositories.QueueItemRepository
import org.springframework.stereotype.Repository

@Repository
class QueueItemDAO(
    private val queueItemRepository: QueueItemRepository
) : DAO<QueueItem, Long>(queueItemRepository) {

  suspend fun getByRabbitIdAndUserId(rabbitId: String, userId: String): QueueItem? {
    return queueItemRepository.findByRabbitIdAndUserEntityId(rabbitId, userId)
  }

}