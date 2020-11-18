package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.Algorithm
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface AlgorithmRepository : R2dbcRepository<Algorithm, Any> {

  @Query("SELECT algorithms.id, algorithms.name, algorithms.md5 FROM algorithms LEFT JOIN algorithm_user ON (algorithm_user.algorithm_id=algorithms.id) WHERE user_entity_id=$1 AND md5=$2")
  suspend fun findByUserIdAndMD5(userId: String, md5: String): Algorithm?

  @Query("SELECT algorithms.id, algorithms.name, algorithms.md5 FROM algorithms LEFT JOIN algorithm_user ON (algorithm_user.algorithm_id=algorithms.id) WHERE user_entity_id=$1")
  suspend fun findByUserId(userId: String): Array<Algorithm>

}