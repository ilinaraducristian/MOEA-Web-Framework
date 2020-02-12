package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.Algorithm
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.entities.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface AlgorithmRepository : CrudRepository<Algorithm, String> {
  fun findByName(name: String): Optional<Algorithm>
  fun findByUserAndName(user: User, name: String): Optional<Algorithm>
  fun findByUser(user: User): List<Algorithm>
}