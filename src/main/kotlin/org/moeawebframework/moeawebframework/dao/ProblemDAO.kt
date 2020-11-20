package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.entities.Problem
import org.moeawebframework.moeawebframework.repositories.ProblemRepository
import org.springframework.stereotype.Repository

@Repository
class ProblemDAO(
    private val problemRepository: ProblemRepository
) : DAO<Problem> {

  override suspend fun get(id: Any): Problem? {
    return problemRepository.findById(id as Long).awaitFirstOrNull()
  }

  override suspend fun getAll(): List<Problem> {
    return problemRepository.findAll().collectList().awaitSingle()
  }

  override suspend fun save(t: Problem): Problem? {
    return problemRepository.save(t).awaitFirstOrNull()
  }

  override suspend fun update(t: Problem, fields: HashMap<String, Any?>) {

  }

  override suspend fun delete(t: Problem) {
    problemRepository.delete(t).awaitSingle()
  }

  suspend fun getByUserEntityId(userId: String): List<Problem> {
    return problemRepository.findByUserEntityId(userId).collectList().awaitSingle()
  }

  suspend fun getByUserEntityIdAndMD5(userId: String, md5: String): Problem? {
    return problemRepository.findByUserEntityIdAndMD5(userId, md5)
  }

}