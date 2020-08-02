package org.moeawebframework.moeawebframework.repositories

import org.moeawebframework.moeawebframework.entities.ProblemSolver
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface ProblemSolverRepository : R2dbcRepository<ProblemSolver, Long> {

  //  @Query("SELECT * FROM problem_solvers WHERE user_id = :userId")
  fun findByUserId(userId: Long): Mono<ProblemSolver>

  @Query("UPDATE problem_solvers SET status = :status WHERE id = :id")
  fun updateStatus(id: Long, status: String)

}