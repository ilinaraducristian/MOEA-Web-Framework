package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.dto.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, Long> {

  fun findByUsername(username: String): Optional<User>
  fun findByEmail(email: String): Optional<User>
  fun findByFirstName(first_name: String): List<User>
  fun findByLastName(last_name: String): List<User>

}