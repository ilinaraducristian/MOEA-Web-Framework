package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.ProblemUser
import org.moeawebframework.moeawebframework.repositories.ProblemUserRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class ProblemUserDAO(
    private val problemUserRepository: ProblemUserRepository
) : DAO<ProblemUser> {
  override fun get(id: Long): Mono<ProblemUser> {
    return problemUserRepository.findById(id)
  }

  override fun getAll(): Flux<ProblemUser> {
    return problemUserRepository.findAll()
  }

  override fun save(t: ProblemUser): Mono<ProblemUser> {
    return problemUserRepository.save(t)
  }

  override fun update(t: ProblemUser, fields: HashMap<String, Any?>): Mono<Void> {
    return Mono.empty()
  }

  override fun delete(t: ProblemUser): Mono<Void> {
    return problemUserRepository.delete(t)
  }

  fun getByUserId(userId: Long): Flux<ProblemUser> {
    return problemUserRepository.findByUserId(userId)
  }

  fun getByUserIdAndProblemId(userId: Long, problemId: Long): Mono<ProblemUser> {
    return problemUserRepository.findByUserIdAndProblemId(userId, problemId)
  }

  fun getByUserUsername(username: String): Flux<ProblemUser> {
    return problemUserRepository.findByUserUsername(username)
  }

}