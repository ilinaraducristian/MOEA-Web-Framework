package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.Algorithm
import org.moeawebframework.moeawebframework.entities.Problem
import org.moeawebframework.moeawebframework.repositories.AlgorithmRepository
import org.moeawebframework.moeawebframework.repositories.ProblemRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class AlgorithmDAO(
    private val algorithmRepository: AlgorithmRepository
) : Dao<Algorithm> {
  override fun get(id: Long): Mono<Algorithm> {
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

  fun existsByMd5(sha256: String): Boolean {
//    return problemRepository.existsByMd5(sha256)
    return true
  }

  fun findBySha256(sha256: String): Mono<Algorithm> {
    return algorithmRepository.findBySha256(sha256)
  }

}