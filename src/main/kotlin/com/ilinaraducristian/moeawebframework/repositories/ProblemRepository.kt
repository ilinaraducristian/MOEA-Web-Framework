package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.entities.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ProblemRepository : CrudRepository<Problem, Long> {
  fun findByName(name: String): Optional<Problem>
  fun findByUserAndName(user: User, name: String): Optional<Problem>
  fun findByUser(user: User): List<Problem>
}