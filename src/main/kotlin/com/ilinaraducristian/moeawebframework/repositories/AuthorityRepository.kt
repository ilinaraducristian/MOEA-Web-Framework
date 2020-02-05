package com.ilinaraducristian.moeawebframework.repositories

import com.ilinaraducristian.moeawebframework.entities.Authority
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface AuthorityRepository : CrudRepository<Authority, String> {
  fun findByUserUsername(username: String): List<Authority>
  fun findByAuthority(authority: String): List<Authority>
}