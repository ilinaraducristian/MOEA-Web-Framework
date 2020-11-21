package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.r2dbc.repository.R2dbcRepository

open class DAO<T, ID>(
    open val r2dbcRepository: R2dbcRepository<T, ID>
) {

  open suspend fun get(id: ID): T? {
    return r2dbcRepository.findById(id).awaitFirstOrNull()
  }

  open suspend fun getAll(): List<T> {
    return r2dbcRepository.findAll().collectList().awaitSingle()
  }

  open suspend fun save(t: T): T? {
    return r2dbcRepository.save(t).awaitFirstOrNull()
  }

  open suspend fun update(t: T, fields: HashMap<String, Any?>) {

  }

  open suspend fun delete(t: T) {
    r2dbcRepository.delete(t).awaitFirstOrNull()
  }


}