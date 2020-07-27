package org.moeawebframework.moeawebframework.entities

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.repositories.UserRepository
import org.springframework.stereotype.Component

@Component
class UserDao(
    private val userRepository: UserRepository
) : Dao<User> {

  override suspend fun get(id: Long): User {
    return userRepository.findById(id).awaitSingle()
  }

  override suspend fun getAll(): Flow<User> {
    return userRepository.findAll().asFlow()
  }

  override suspend fun save(t: User): User {
    return userRepository.save(t).awaitSingle()
  }

  override suspend fun delete(t: User) {
    userRepository.delete(t).awaitSingle()
  }

  suspend fun findByUsername(username: String): User {
    return userRepository.findByUsername(username)
  }

}