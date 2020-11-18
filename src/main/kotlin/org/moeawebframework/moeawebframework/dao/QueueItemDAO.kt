package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.QueueItem
import org.moeawebframework.moeawebframework.repositories.QueueItemRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class QueueItemDAO(
    private val queueItemRepository: QueueItemRepository
) : DAO<QueueItem> {

  override fun get(id: Any): Mono<QueueItem> {
    return queueItemRepository.findById(id as Long)
  }

  override fun getAll(): Flux<QueueItem> {
    return queueItemRepository.findAll()
  }

  override fun save(t: QueueItem): Mono<QueueItem> {
    return queueItemRepository.save(t)
  }

  override fun update(t: QueueItem, fields: HashMap<String, Any?>): Mono<Void> {
    var modified = false
    if (fields.containsKey("status")) {
      if (fields["status"] == null) return Mono.error(RuntimeException("Status cannot be null"))
      t.status = fields["status"] as String
      modified = true
    }
    if (modified) {
      return save(t).flatMap { Mono.empty<Void>() }
    } else {
      return Mono.empty()
    }
  }

  override fun delete(t: QueueItem): Mono<Void> {
    return queueItemRepository.delete(t)
  }

  suspend fun getByRabbitIdAndUserId(rabbitId: String, userId: String): QueueItem? {
    return queueItemRepository.findByRabbitIdAndUserId(rabbitId, userId)
  }

}