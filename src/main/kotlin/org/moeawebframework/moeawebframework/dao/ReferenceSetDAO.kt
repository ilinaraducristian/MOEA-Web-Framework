package org.moeawebframework.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.entities.ReferenceSet
import org.moeawebframework.moeawebframework.repositories.ReferenceSetRepository
import org.springframework.stereotype.Repository

@Repository
class ReferenceSetDAO(
    private val referenceSetRepository: ReferenceSetRepository
) : DAO<ReferenceSet> {

  override suspend fun get(id: Any): ReferenceSet? {
    return referenceSetRepository.findById(id as Long).awaitFirstOrNull()
  }

  override suspend fun getAll(): List<ReferenceSet> {
    return referenceSetRepository.findAll().collectList().awaitSingle()
  }

  override suspend fun save(t: ReferenceSet): ReferenceSet? {
    return referenceSetRepository.save(t).awaitFirstOrNull()
  }

  override suspend fun update(t: ReferenceSet, fields: HashMap<String, Any?>) {

  }

  override suspend fun delete(t: ReferenceSet) {
    referenceSetRepository.delete(t).awaitSingle()
  }

  suspend fun getByUserEntityId(userId: String): List<ReferenceSet> {
    return referenceSetRepository.findByUserEntityId(userId).collectList().awaitSingle()
  }

  suspend fun getByUserEntityIdAndMD5(userId: String, md5: String): ReferenceSet? {
    return referenceSetRepository.findByUserEntityIdAndMD5(userId, md5)
  }

}