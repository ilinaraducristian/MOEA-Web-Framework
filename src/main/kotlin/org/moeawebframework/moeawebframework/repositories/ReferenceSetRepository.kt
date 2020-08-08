package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.ReferenceSet
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface ReferenceSetRepository : R2dbcRepository<ReferenceSet, Long> {

  fun findBySha256(sha256: String): Mono<ReferenceSet>

  fun existsBySha256(sha256: String): Boolean

}