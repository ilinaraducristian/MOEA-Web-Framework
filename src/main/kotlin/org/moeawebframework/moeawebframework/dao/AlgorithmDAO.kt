package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.Algorithm
import org.moeawebframework.moeawebframework.repositories.AlgorithmRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class AlgorithmDAO(
    private val algorithmRepository: AlgorithmRepository
) : DAO<Algorithm> {

  override fun get(id: Any): Mono<Algorithm> {
    return algorithmRepository.findById(id)
  }

  override fun getAll(): Flux<Algorithm> {
    return algorithmRepository.findAll()
  }

  override fun save(t: Algorithm): Mono<Algorithm> {
    return algorithmRepository.save(t)
  }

  override fun update(t: Algorithm, fields: HashMap<String, Any?>): Mono<Void> {
    return Mono.empty()
  }

  override fun delete(t: Algorithm): Mono<Void> {
    return algorithmRepository.delete(t)
  }

  suspend fun getByUserId(userId: String): Array<Algorithm> {
    return algorithmRepository.findByUserId(userId)
  }

  suspend fun getByUserIdAndMD5(userId: String, md5: String): Algorithm? {
    return algorithmRepository.findByUserIdAndMD5(userId, md5)
  }

}