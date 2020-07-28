package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.Problem
import org.moeawebframework.moeawebframework.repositories.ProblemRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class ProblemDAO(
    private val problemRepository: ProblemRepository
) : Dao<Problem> {
  override fun get(id: Long): Mono<Problem> {
    return problemRepository.findById(id)
  }

  override fun getAll(): Flux<Problem> {
    return problemRepository.findAll()
  }

  override fun save(t: Problem): Mono<Problem> {
    return problemRepository.save(t)
  }

  override fun update(t: Problem, fields: HashMap<String, Any?>): Mono<Void> {
    return Mono.empty()
  }

  override fun delete(t: Problem): Mono<Void> {
    return problemRepository.delete(t)
  }

  fun existsByMd5(sha256: String): Boolean {
//    return problemRepository.existsByMd5(sha256)
    return true
  }

  fun findBySha256(sha256: String): Mono<Problem> {
    return problemRepository.findBySha256(sha256)
  }

}