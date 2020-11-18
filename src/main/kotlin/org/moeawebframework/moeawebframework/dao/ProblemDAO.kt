package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.Problem
import org.moeawebframework.moeawebframework.repositories.ProblemRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class ProblemDAO(
    private val problemRepository: ProblemRepository
) : DAO<Problem> {

  override fun get(id: Any): Mono<Problem> {
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

  suspend fun getByUserId(userId: String): Array<Problem> {
    return problemRepository.findByUserId(userId)
  }

  suspend fun getByUserIdAndMD5(userId: String, md5: String): Problem? {
    return problemRepository.findByUserIdAndMD5(userId, md5)
  }

}