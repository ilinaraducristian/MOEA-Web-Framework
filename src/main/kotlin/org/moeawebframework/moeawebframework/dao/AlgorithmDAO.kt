package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.entities.Algorithm
import org.moeawebframework.moeawebframework.repositories.AlgorithmRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class AlgorithmDAO(
    private val algorithmRepository: AlgorithmRepository
) : DAO<Algorithm> {

  override suspend fun get(id: Any): Algorithm? {
    return algorithmRepository.findById(id as Long).awaitFirstOrNull()
  }

  override suspend fun getAll(): List<Algorithm> {
    return algorithmRepository.findAll().collectList().awaitSingle()
  }

  override suspend fun save(t: Algorithm): Algorithm? {
    return algorithmRepository.save(t).awaitFirstOrNull()
  }

  override suspend fun update(t: Algorithm, fields: HashMap<String, Any?>) {

  }

  override suspend fun delete(t: Algorithm) {
    algorithmRepository.delete(t).awaitSingle()
  }

  suspend fun getByUserEntityId(userId: String): List<Algorithm> {
    return algorithmRepository.findByUserEntityId(userId).collectList().awaitSingle()
  }

  suspend fun getByUserEntityIdAndMD5(userId: String, md5: String): Algorithm? {
    return algorithmRepository.findByUserEntityIdAndMD5(userId, md5)
  }

}