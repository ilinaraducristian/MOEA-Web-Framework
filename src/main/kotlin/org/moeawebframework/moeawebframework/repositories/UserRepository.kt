package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.User
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface UserRepository : R2dbcRepository<User, Long> {

  fun findByUsername(username: String): Mono<User>

}