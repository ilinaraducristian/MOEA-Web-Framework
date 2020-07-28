package org.moeawebframework.moeawebframework.dao

import org.moeawebframework.moeawebframework.entities.Problem
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
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

  override fun update(t: User, fields: HashMap<String, Any?>): Mono<Void> {
    return Mono.empty()
  }

  override fun delete(t: User): Mono<Void> {
    return userRepository.delete(t)
  }

  fun getByUsername(username: String): Mono<User> {
    return userRepository.findByUsername(username)
  }

}