package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.AlgorithmUser
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AlgorithmUserRepository : R2dbcRepository<AlgorithmUser, Long> {

  fun findByUserId(userId: Long): Flux<AlgorithmUser>
  fun findByUserIdAndAlgorithmId(userId: Long, AlgorithmId: Long): Mono<AlgorithmUser>

  @Query("SELECT * FROM algorithms_users WHERE user_id = (SELECT id FROM users WHERE username = :username)")
  fun findByUserUsername(username: String): Flux<AlgorithmUser>
}