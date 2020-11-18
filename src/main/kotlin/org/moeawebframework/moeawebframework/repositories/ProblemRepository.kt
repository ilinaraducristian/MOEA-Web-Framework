package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.Problem
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface ProblemRepository : R2dbcRepository<Problem, Any> {

  @Query("SELECT problems.id, problems.name, problems.md5 FROM problems LEFT JOIN problem_user ON (problem_user.problem_id=problems.id) WHERE user_entity_id=$1 AND md5=$2")
  suspend fun findByUserIdAndMD5(userId: String, md5: String): Problem?

  @Query("SELECT problems.id, problems.name, problems.md5 FROM problems LEFT JOIN problem_user ON (problem_user.problem_id=problems.id) WHERE user_entity_id=$1")
  suspend fun findByUserId(userId: String): Array<Problem>

}