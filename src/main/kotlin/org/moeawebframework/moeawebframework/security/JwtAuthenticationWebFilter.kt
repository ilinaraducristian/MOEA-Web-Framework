package org.moeawebframework.moeawebframework.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import reactor.core.publisher.Mono

class JwtAuthenticationWebFilter(reactiveAuthenticationManager: ReactiveAuthenticationManager) : AuthenticationWebFilter(reactiveAuthenticationManager) {
  init {
    setServerAuthenticationConverter { exchange ->
      Mono.justOrEmpty(exchange.request.headers["Authorization"])
          .flatMap {
            if (it[0].startsWith("Bearer ")) {
              return@flatMap Mono.just(it[0].replace("Bearer ", ""))
            } else {
              return@flatMap Mono.empty<String>()
            }
          }
          .map {
            UsernamePasswordAuthenticationToken(it, it)
          }
    }
  }

}