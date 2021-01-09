package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.entities.ReferenceSet
import org.moeawebframework.moeawebframework.repositories.ReferenceSetRepository
import org.springframework.stereotype.Repository

@Repository
class ReferenceSetDAO(
    private val referenceSetRepository: ReferenceSetRepository
) : DAO<ReferenceSet, Long>(referenceSetRepository) {

  suspend fun getByUserEntityId(userId: String): List<ReferenceSet> {
    return referenceSetRepository.findByUserEntityId(userId).collectList().awaitSingle()
  }

  suspend fun getByUserEntityIdAndMD5(userId: String, md5: String): ReferenceSet? {
    return referenceSetRepository.findByUserEntityIdAndMD5(userId, md5)
  }

}