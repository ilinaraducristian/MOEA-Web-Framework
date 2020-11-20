package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitLast
import org.moeawebframework.moeawebframework.entities.QueueItem
import org.moeawebframework.moeawebframework.repositories.QueueItemRepository
import org.springframework.stereotype.Repository

@Repository
class QueueItemDAO(
    private val queueItemRepository: QueueItemRepository
) : DAO<QueueItem> {

  override suspend fun get(id: Any): QueueItem? {
    return queueItemRepository.findById(id as Long).awaitLast()
  }

  override suspend fun getAll(): List<QueueItem> {
    return queueItemRepository.findAll().collectList().awaitLast()
  }

  override suspend fun save(t: QueueItem): QueueItem? {
    return queueItemRepository.save(t).awaitLast()
  }

  override suspend fun update(t: QueueItem, fields: HashMap<String, Any?>) {
    if (fields.containsKey("status")) {
      if (fields["status"] == null) throw RuntimeException("Status cannot be null")
      t.status = fields["status"] as String
      save(t)
    }
  }

  override suspend fun delete(t: QueueItem) {
    queueItemRepository.delete(t).awaitLast()
  }

  suspend fun getByRabbitIdAndUserId(rabbitId: String, userId: String): QueueItem? {
    return queueItemRepository.findByRabbitIdAndUserEntityId(rabbitId, userId)
  }

}