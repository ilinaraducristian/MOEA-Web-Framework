package org.moeawebframework.moeawebframework.dao

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DAO<T> {

  fun get(id: Any): Mono<T>

  fun getAll(): Flux<T>

  fun save(t: T): Mono<T>

  fun update(t: T, fields: HashMap<String, Any?>): Mono<Void>

  fun delete(t: T): Mono<Void>

}