package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.dto.Problem
import com.ilinaraducristian.moeawebframework.dto.User
import org.springframework.data.repository.CrudRepository

interface ProblemRepository : CrudRepository<Problem, Long> {
  fun findByUserUsername(userName: String): List<Problem>
  fun findByUserAndId(user: User, id: Long): Problem?
  fun existsByUserAndUserDefinedName(user: User, userDefinedName: String): Boolean
}