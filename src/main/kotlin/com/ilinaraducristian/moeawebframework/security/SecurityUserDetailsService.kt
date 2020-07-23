package com.ilinaraducristian.moeawebframework.security

import com.ilinaraducristian.moeawebframework.repositories.AuthorityRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class SecurityUserDetailsService(
    private val userRepo: UserRepository,
    private val authorityRepo: AuthorityRepository
) : UserDetailsService {

  override fun loadUserByUsername(username: String): UserDetails {
    val user = userRepo.findByUsername(username) ?: throw UsernameNotFoundException("""$username not found""")
    val authorities = authorityRepo.findByUserUsername(user.username)
    if (authorities.isEmpty()) {
      throw UsernameNotFoundException("""$username has no granted authorities""")
    }
    return UserPrincipal(user, authorities.map { SimpleGrantedAuthority(it.authority) }.toMutableSet())
  }

}