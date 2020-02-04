package com.ilinaraducristian.moeawebframework.security

import com.ilinaraducristian.moeawebframework.repositories.AuthorityRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList

@Service
class SecurityUserDetailsService(
    private val encoder: BCryptPasswordEncoder,
    private val userRepo: UserRepository,
    private val authorityRepo: AuthorityRepository
) : UserDetailsService {

  @Value("\${spring.profiles.active}")
  private val activeProfile: String? = null

  override fun loadUserByUsername(username: String): UserDetails? {
    if (activeProfile == "dev") {
      // dev
      val grantedAuthorizations = ArrayList<GrantedAuthority>()
      grantedAuthorizations.add(SimpleGrantedAuthority("USER"))
      return User("user", encoder.encode("password"), grantedAuthorizations)
    } else if (activeProfile == "prod") {
      return userRepo.findByUsername(username).flatMap { user ->
        val authorities = authorityRepo.findByUserUsername(user.username)
        if (authorities.isEmpty())
          return@flatMap Optional.empty<UserDetails>()
        else {
          return@flatMap Optional.of(UserPrincipal(user, authorities.map { SimpleGrantedAuthority(it.authority) }.toMutableSet()))
        }
      }.orElse(null)
    } else {
      return null
    }
  }

}