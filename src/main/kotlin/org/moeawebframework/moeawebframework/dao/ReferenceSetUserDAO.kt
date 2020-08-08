package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.ReferenceSetUser
import org.moeawebframework.moeawebframework.repositories.ReferenceSetUserRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class ReferenceSetUserDAO(
    private val referenceSetUserRepository: ReferenceSetUserRepository
) : DAO<ReferenceSetUser> {
  override fun get(id: Long): Mono<ReferenceSetUser> {
    return referenceSetUserRepository.findById(id)
  }

  override fun getAll(): Flux<ReferenceSetUser> {
    return referenceSetUserRepository.findAll()
  }

  override fun save(t: ReferenceSetUser): Mono<ReferenceSetUser> {
    return referenceSetUserRepository.save(t)
  }

  override fun update(t: ReferenceSetUser, fields: HashMap<String, Any?>): Mono<Void> {
    return Mono.empty()
  }

  override fun delete(t: ReferenceSetUser): Mono<Void> {
    return referenceSetUserRepository.delete(t)
  }

  fun getByUserId(userId: Long): Flux<ReferenceSetUser> {
    return referenceSetUserRepository.findByUserId(userId)
  }

  fun getByUserIdAndReferenceSetId(userId: Long, referenceSetId: Long): Mono<ReferenceSetUser> {
    return referenceSetUserRepository.findByUserIdAndReferenceSetId(userId, referenceSetId)
  }

  fun getByUserUsername(username: String): Flux<ReferenceSetUser> {
    return referenceSetUserRepository.findByUserUsername(username)
  }

}