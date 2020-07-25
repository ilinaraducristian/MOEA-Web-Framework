package org.moeawebframework.moeawebframework.security

import org.moeawebframework.moeawebframework.utils.validateJwt
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager : ReactiveAuthenticationManager {
  override fun authenticate(authentication: Authentication): Mono<Authentication> {
    return Mono.just(authentication)
        .map { validateJwt(it.credentials as String) }
        .onErrorResume { Mono.empty() }
        .map { jws ->
          UsernamePasswordAuthenticationToken(
              jws.body.subject,
              authentication.credentials as String,
              mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
          )
        }
  }
}