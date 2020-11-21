package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.r2dbc.repository.R2dbcRepository

open class DAO<T, ID>(
    private val r2dbcRepository: R2dbcRepository<T, ID>
) {

  suspend fun get(id: ID): T? {
    return r2dbcRepository.findById(id).awaitFirstOrNull()
  }

  suspend fun getAll(): List<T> {
    return r2dbcRepository.findAll().collectList().awaitSingle()
  }

  suspend fun save(t: T): T? {
    return r2dbcRepository.save(t).awaitFirstOrNull()
  }

  suspend fun update(t: T, fields: HashMap<String, Any?>) {

  }

  suspend fun delete(t: T) {
    r2dbcRepository.delete(t).awaitSingle()
  }


}