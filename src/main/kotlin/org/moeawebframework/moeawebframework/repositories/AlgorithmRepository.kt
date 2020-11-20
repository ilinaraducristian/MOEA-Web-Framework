package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.Algorithm
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface AlgorithmRepository : R2dbcRepository<Algorithm, Long> {

  @Query("SELECT algorithms.id, algorithms.name, algorithms.md5 FROM algorithms LEFT JOIN algorithm_user_entity ON (algorithm_user_entity.algorithm_id=algorithms.id) WHERE user_entity_id=$1 AND md5=$2")
  suspend fun findByUserEntityIdAndMD5(userEntityId: String, md5: String): Algorithm?

  @Query("SELECT algorithms.id, algorithms.name, algorithms.md5 FROM algorithms LEFT JOIN algorithm_user_entity ON (algorithm_user_entity.algorithm_id=algorithms.id) WHERE user_entity_id=$1")
  suspend fun findByUserEntityId(userEntityId: String): Flux<Algorithm>

}