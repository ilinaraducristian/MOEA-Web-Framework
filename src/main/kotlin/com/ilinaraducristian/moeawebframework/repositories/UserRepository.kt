package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.dto.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {

  fun findByUsername(username: String): User?
  fun findByEmail(email: String): User?
  fun findByFirstName(first_name: String): List<User>
  fun findByLastName(last_name: String): List<User>

}