package org.moeawebframework.moeawebframework.security

import kotlinx.coroutines.reactor.mono
import org.moeawebframework.moeawebframework.entities.UserDao
import org.moeawebframework.moeawebframework.utils.validateJwt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


class JwtReactiveAuthenticationManager : ReactiveAuthenticationManager {

  @Autowired
  lateinit var userDao: UserDao

  override fun authenticate(authentication: Authentication): Mono<Authentication> {
    return Mono.just(authentication)
        .map { validateJwt(it.credentials as String) }
        .onErrorResume { Mono.error(BadCredentialsException("Authorization header not present")) }
        .flatMap { jws -> mono{userDao.findByUsername(jws.body.subject)} }
        .map { user ->
          UsernamePasswordAuthenticationToken(
              user.username,
              authentication.credentials as String,
              mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
          )
        }
  }
}