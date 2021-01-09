package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.entities.Algorithm
import org.moeawebframework.moeawebframework.repositories.AlgorithmRepository
import org.springframework.stereotype.Repository

@Repository
class AlgorithmDAO(
  private val algorithmRepository: AlgorithmRepository
) : DAO<Algorithm, Long>(algorithmRepository) {

  suspend fun getByUserEntityId(userId: String): List<Algorithm> {
    return algorithmRepository.findByUserEntityId(userId).collectList().awaitSingle()
  }

  suspend fun getByUserEntityIdAndMD5(userId: String, md5: String): Algorithm? {
    return algorithmRepository.findByUserEntityIdAndMD5(userId, md5)
  }

}