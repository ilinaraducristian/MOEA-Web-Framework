package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.ReferenceSet
import org.moeawebframework.moeawebframework.repositories.ReferenceSetRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class ReferenceSetDAO(
    private val referenceSetRepository: ReferenceSetRepository
) : DAO<ReferenceSet> {

  override fun get(id: Any): Mono<ReferenceSet> {
    return referenceSetRepository.findById(id)
  }

  override fun getAll(): Flux<ReferenceSet> {
    return referenceSetRepository.findAll()
  }

  override fun save(t: ReferenceSet): Mono<ReferenceSet> {
    return referenceSetRepository.save(t)
  }

  override fun update(t: ReferenceSet, fields: HashMap<String, Any?>): Mono<Void> {
    return Mono.empty()
  }

  override fun delete(t: ReferenceSet): Mono<Void> {
    return referenceSetRepository.delete(t)
  }

  suspend fun getByUserId(userId: String): Array<ReferenceSet> {
    return referenceSetRepository.findByUserId(userId)
  }

  suspend fun getByUserIdAndMD5(userId: String, md5: String): ReferenceSet? {
    return referenceSetRepository.findByUserIdAndMD5(userId, md5)
  }

}