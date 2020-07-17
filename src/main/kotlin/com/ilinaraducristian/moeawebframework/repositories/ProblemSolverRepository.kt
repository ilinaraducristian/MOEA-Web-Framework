package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.ProblemSolver
import com.ilinaraducristian.moeawebframework.entities.User
import org.springframework.data.repository.CrudRepository

interface ProblemSolverRepository : CrudRepository<ProblemSolver, String> {
  fun existsByUserAndRabbitId(user: User, rabbitId: String): Boolean
  fun deleteByUserUsernameAndRabbitId(username: String, rabbitId: String): ProblemSolver?
  fun findByUserUsernameAndRabbitId(username: String, rabbitId: String): ProblemSolver?
}