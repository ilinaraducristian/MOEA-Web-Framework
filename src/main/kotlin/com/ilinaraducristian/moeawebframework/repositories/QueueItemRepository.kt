package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.Authority
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.entities.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface QueueItemRepository : CrudRepository<QueueItem, String> {
  fun existsByUserAndRabbitId(user: User, rabbitId: String): Boolean
  fun deleteByUserUsernameAndRabbitId(username: String, rabbitId: String): Optional<QueueItem>
  fun findByUserUsernameAndRabbitId(username: String, rabbitId: String): Optional<QueueItem>
}