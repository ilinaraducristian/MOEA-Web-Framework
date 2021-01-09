package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.ReferenceSet
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface ReferenceSetRepository : R2dbcRepository<ReferenceSet, Long> {

  @Query("SELECT reference_sets.id, reference_sets.name, reference_sets.md5 FROM reference_sets LEFT JOIN reference_set_user_entity ON (reference_set_user_entity.reference_set_id=reference_sets.id) WHERE user_entity_id=$1 AND md5=$2")
  suspend fun findByUserEntityIdAndMD5(userEntityId: String, md5: String): ReferenceSet?

  @Query("SELECT reference_sets.id, reference_sets.name, reference_sets.md5 FROM reference_sets LEFT JOIN reference_set_user_entity ON (reference_set_user_entity.reference_set_id=reference_sets.id) WHERE user_entity_id=$1")
  suspend fun findByUserEntityId(userEntityId: String): Flux<ReferenceSet>

}