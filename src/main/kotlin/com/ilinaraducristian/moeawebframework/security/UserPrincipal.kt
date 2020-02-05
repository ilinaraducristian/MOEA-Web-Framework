package com.ilinaraducristian.moeawebframework.security

import com.ilinaraducristian.moeawebframework.entities.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(private val user: User, private val grantedAuthorities: MutableCollection<GrantedAuthority>) : UserDetails {

  override fun getAuthorities(): MutableCollection<GrantedAuthority> {
    return grantedAuthorities
  }

  override fun isEnabled(): Boolean {
    return user.enabled
  }

  override fun getUsername(): String {
    return user.username
  }

  override fun isCredentialsNonExpired(): Boolean {
    return user.enabled
  }

  override fun getPassword(): String {
    return user.password
  }

  override fun isAccountNonExpired(): Boolean {
    return user.enabled
  }

  override fun isAccountNonLocked(): Boolean {
    return user.enabled
  }
}