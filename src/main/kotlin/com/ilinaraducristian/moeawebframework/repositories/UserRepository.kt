package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

interface UserRepository : CrudRepository<User, Long> {

  fun findByUsername(username: String): Optional<User>
  fun findByEmail(email: String): Optional<User>
  fun findByFirstName(first_name: String): List<User>
  fun findByLastName(last_name: String): List<User>

}