package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.dto.Problem
import org.springframework.data.repository.CrudRepository

interface ProblemRepository : CrudRepository<Problem, Long>