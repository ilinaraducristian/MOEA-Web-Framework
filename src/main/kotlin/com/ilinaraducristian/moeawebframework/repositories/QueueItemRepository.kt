package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.entities.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface QueueItemRepository : CrudRepository<QueueItem, String> {
  fun existsByUserAndRabbitId(user: User, rabbitId: String): Boolean
  fun findByUserAndRabbitId(user: User, rabbitId: String): Optional<QueueItem>
}