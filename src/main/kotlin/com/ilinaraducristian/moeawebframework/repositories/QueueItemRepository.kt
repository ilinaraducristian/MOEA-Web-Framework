package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.entities.User
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface QueueItemRepository : CrudRepository<QueueItem, String> {
  fun existsByUserAndRabbitId(user: User, rabbitId: String): Boolean
  @Transactional
  fun deleteByUserUsernameAndRabbitId(username: String, rabbitId: String): Long
  fun findByUserUsernameAndRabbitId(username: String, rabbitId: String): QueueItem?
}