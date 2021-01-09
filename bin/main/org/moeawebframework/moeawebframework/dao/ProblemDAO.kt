package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.entities.Problem
import org.moeawebframework.moeawebframework.repositories.ProblemRepository
import org.springframework.stereotype.Repository

@Repository
class ProblemDAO(
    private val problemRepository: ProblemRepository
) : DAO<Problem, Long>(problemRepository) {

  suspend fun getByUserEntityId(userId: String): List<Problem> {
    return problemRepository.findByUserEntityId(userId).collectList().awaitSingle()
  }

  suspend fun getByUserEntityIdAndMD5(userId: String, md5: String): Problem? {
    return problemRepository.findByUserEntityIdAndMD5(userId, md5)
  }

}