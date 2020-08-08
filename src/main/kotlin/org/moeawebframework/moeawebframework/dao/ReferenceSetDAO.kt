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
  override fun get(id: Long): Mono<ReferenceSet> {
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

  fun getBySha256(sha256: String): Mono<ReferenceSet> {
    return referenceSetRepository.findBySha256(sha256)
  }

  fun existsBySha256(sha256: String): Boolean {
    return referenceSetRepository.existsBySha256(sha256)
  }

}