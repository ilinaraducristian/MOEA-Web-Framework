package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.Problem
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface ProblemRepository : R2dbcRepository<Problem, Long> {

  fun findBySha256(sha256: String): Mono<Problem>

}