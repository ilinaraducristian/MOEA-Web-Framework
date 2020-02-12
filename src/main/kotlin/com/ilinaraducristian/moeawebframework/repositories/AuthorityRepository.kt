package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.Authority
import org.springframework.data.repository.CrudRepository

interface AuthorityRepository : CrudRepository<Authority, String> {
  fun findByUserUsername(username: String): List<Authority>
}