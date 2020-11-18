package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.ReferenceSet
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface ReferenceSetRepository : R2dbcRepository<ReferenceSet, Any> {

  @Query("SELECT reference_sets.id, reference_sets.name, reference_sets.md5 FROM reference_sets LEFT JOIN reference_set_user ON (reference_set_user.reference_set_id=reference_sets.id) WHERE user_entity_id=$1 AND md5=$2")
  suspend fun findByUserIdAndMD5(userId: String, md5: String): ReferenceSet?

  @Query("SELECT reference_sets.id, reference_sets.name, reference_sets.md5 FROM reference_sets LEFT JOIN reference_set_user ON (reference_set_user.reference_set_id=reference_sets.id) WHERE user_entity_id=$1")
  suspend fun findByUserId(userId: String): Array<ReferenceSet>

}