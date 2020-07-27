package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.User
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : R2dbcRepository<User, Long> {

  @Query("SELECT * FROM USERS WHERE username = :username")
  suspend fun findByUsername(username: String): User

}