package org.moeawebframework.moeawebframework.security

import org.moeawebframework.moeawebframework.entities.UserDao
import org.moeawebframework.moeawebframework.utils.validateJwt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.onErrorMap


class JwtReactiveAuthenticationManager : ReactiveAuthenticationManager {

  @Autowired
  lateinit var userDao: UserDao

  override fun authenticate(authentication: Authentication?): Mono<Authentication> {
    println("AUTH:")
    println(authentication)
    return Mono.justOrEmpty(authentication)
        .map { validateJwt(it.credentials as String) }
        .onErrorResume { Mono.empty() }
        .flatMap {jws ->
          println("AM AJUNS AICI 1")
          userDao.findByUsername(jws.body.subject)
        }
        .map { user ->
          println("AM AJUNS AICI 2")
          return@map UsernamePasswordAuthenticationToken(
              user.username,
              authentication!!.credentials as String,
              mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
          )
        }
  }
}