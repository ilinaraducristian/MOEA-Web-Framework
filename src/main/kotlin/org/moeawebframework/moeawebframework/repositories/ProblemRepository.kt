package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.Problem
import org.moeawebframework.moeawebframework.entities.User
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ProblemRepository : R2dbcRepository<Problem, Long> {

//  @Query("SELECT * FROM USERS WHERE username = :username")
  fun existsBySha256(sha256: String): Boolean

  fun findBySha256(sha256: String): Mono<Problem>

}