package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.AlgorithmUser
import org.moeawebframework.moeawebframework.repositories.AlgorithmUserRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class AlgorithmUserDAO(
    private val algorithmUserRepository: AlgorithmUserRepository
) : Dao<AlgorithmUser> {
  override fun get(id: Long): Mono<AlgorithmUser> {
    return algorithmUserRepository.findById(id)
  }

  override fun getAll(): Flux<AlgorithmUser> {
    return algorithmUserRepository.findAll()
  }

  override fun save(t: AlgorithmUser): Mono<AlgorithmUser> {
    return algorithmUserRepository.save(t)
  }

  override fun update(t: AlgorithmUser, fields: HashMap<String, Any?>): Mono<Void> {
    return Mono.empty()
  }

  override fun delete(t: AlgorithmUser): Mono<Void> {
    return algorithmUserRepository.delete(t)
  }

  fun getByUserId(userId: Long): Flux<AlgorithmUser> {
    return algorithmUserRepository.findByUserId(userId)
  }

  fun getByUserIdAndAlgorithmId(userId: Long, algorithmId: Long): Mono<AlgorithmUser> {
    return algorithmUserRepository.findByUserIdAndAlgorithmId(userId, algorithmId)
  }

  fun getByUserUsername(username: String): Flux<AlgorithmUser> {
    return algorithmUserRepository.findByUserUsername(username)
  }

}