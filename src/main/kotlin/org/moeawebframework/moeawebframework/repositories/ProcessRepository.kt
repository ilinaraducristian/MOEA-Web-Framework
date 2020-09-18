package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.Process
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface ProcessRepository : R2dbcRepository<Process, Long> {

  fun findByRabbitId(rabbitId: String): Mono<Process>

}