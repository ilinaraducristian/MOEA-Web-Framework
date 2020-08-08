package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.Algorithm
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface AlgorithmRepository : R2dbcRepository<Algorithm, Long> {

  fun findBySha256(sha256: String): Mono<Algorithm>

  fun existsBySha256(sha256: String): Boolean

}