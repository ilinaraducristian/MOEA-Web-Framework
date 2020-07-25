package org.moeawebframework.moeawebframework.entities

import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class UserDao(
  private val userRepository: UserRepository
) : Dao<User> {

  override fun get(id: Long): Mono<User> {
    return userRepository.findById(id)
  }

  override fun getAll(): Flux<User> {
    return userRepository.findAll()
  }

  override fun save(t: User): Mono<User> {
    return userRepository.save(t)
  }

  override fun update(t: User, params: Array<String?>): Mono<User> {
    t.username = params[0]!!
    return userRepository.save(t)
  }

  override fun delete(t: User): Mono<Void> {
    return userRepository.delete(t)
  }
}