package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, Long> {
  fun findByUsername(username: String): Optional<User>
}