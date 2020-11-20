package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class UserDAO(
    private val userRepository: UserRepository
) : DAO<User> {

  override suspend fun get(id: Any): User? {
    return userRepository.findById(id as String).awaitFirstOrNull()
  }

  override suspend fun getAll(): List<User> {
    return userRepository.findAll().collectList().awaitSingle()
  }

  override suspend fun save(t: User): User? {
    return userRepository.save(t).awaitFirstOrNull()
  }

  override suspend fun update(t: User, fields: HashMap<String, Any?>) {

  }

  override suspend fun delete(t: User) {
    userRepository.delete(t).awaitSingle()
  }

}