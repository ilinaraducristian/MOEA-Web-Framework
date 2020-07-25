package org.moeawebframework.moeawebframework.security

import org.moeawebframework.moeawebframework.utils.validateJwt
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import reactor.core.publisher.Mono

class JwtAuthenticationWebFilter(reactiveAuthenticationManager: ReactiveAuthenticationManager) : AuthenticationWebFilter(reactiveAuthenticationManager) {

  init {
    setServerAuthenticationConverter { exchange ->
//      return@setServerAuthenticationConverter Mono.empty<Authentication>()
//      Mono.justOrEmpty(exchange)
//          .flatMap { Mono.justOrEmpty(exchange.request.headers["Authorization"]) }
//          .filter { it.isNotEmpty() }
//          .map { UsernamePasswordAuthenticationToken(it[0], it[0]) }.defaultIfEmpty()
      return@setServerAuthenticationConverter Mono.justOrEmpty(exchange)
          .flatMap { Mono.justOrEmpty(exchange.request.headers["Authorization"]) }
          .filter { it.isNotEmpty() }
          .map {
            println("IS NOT EMPTY")
            println(it[0])
            UsernamePasswordAuthenticationToken(it[0], it[0]) }
    }
  }

}