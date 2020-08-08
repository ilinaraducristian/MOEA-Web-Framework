package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.AlgorithmUser
import org.moeawebframework.moeawebframework.entities.ReferenceSet
import org.moeawebframework.moeawebframework.entities.ReferenceSetUser
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ReferenceSetUserRepository : R2dbcRepository<ReferenceSetUser, Long> {

  fun findByUserId(userId: Long): Flux<ReferenceSetUser>
  fun findByUserIdAndReferenceSetId(userId: Long, AlgorithmId: Long): Mono<ReferenceSetUser>

  @Query("SELECT * FROM reference_sets_users WHERE user_id = (SELECT id FROM users WHERE username = :username)")
  fun findByUserUsername(username: String): Flux<ReferenceSetUser>
}