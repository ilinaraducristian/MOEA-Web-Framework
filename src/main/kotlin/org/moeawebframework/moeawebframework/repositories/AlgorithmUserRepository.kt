package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.AlgorithmUser
import org.moeawebframework.moeawebframework.entities.ProblemUser
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface AlgorithmUserRepository : R2dbcRepository<AlgorithmUser, Long> {

  fun findByUserId(userId: Long): Mono<AlgorithmUser>

}