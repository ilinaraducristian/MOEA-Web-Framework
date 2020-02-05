package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.entities.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

interface ProblemRepository : CrudRepository<Problem, Long> {
  fun findByUserUsername(userName: String): List<Problem>
  fun findByUserAndId(user: User, id: Long): Optional<Problem>
  fun existsByUserAndUserDefinedName(user: User, userDefinedName: String): Boolean
}