package org.moeawebframework.moeawebframework.entities

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface Dao<T> {

  fun get(id: Long): Mono<T>

  fun getAll(): Flux<T>

  fun save(t: T): Mono<User>

  fun update(t: T, params: Array<String?>): Mono<User>

  fun delete(t: T): Mono<Void>

}