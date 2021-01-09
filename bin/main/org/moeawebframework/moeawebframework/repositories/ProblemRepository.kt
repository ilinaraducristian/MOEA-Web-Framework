package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.Problem
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface ProblemRepository : R2dbcRepository<Problem, Long> {

  @Query("SELECT problems.id, problems.name, problems.md5 FROM problems LEFT JOIN problem_user_entity ON (problem_user_entity.problem_id=problems.id) WHERE user_entity_id=$1 AND md5=$2")
  suspend fun findByUserEntityIdAndMD5(userEntityId: String, md5: String): Problem?

  @Query("SELECT problems.id, problems.name, problems.md5 FROM problems LEFT JOIN problem_user_entity ON (problem_user_entity.problem_id=problems.id) WHERE user_entity_id=$1")
  suspend fun findByUserEntityId(userEntityId: String): Flux<Problem>

}