package org.moeawebframework.moeawebframework.security

import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
class SecurityConfig {

  @Bean
  fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    val jwtFilter = JwtFilter(JwtAuthenticationManager())
    jwtFilter.setServerAuthenticationConverter{exchange ->
      Mono.justOrEmpty(exchange)
          .flatMap { Mono.justOrEmpty(it.request.cookies["X-Auth"]) }
          .filter { it.isNotEmpty() }
          .map { it[0].value }
          .map { UsernamePasswordAuthenticationToken(it, it) }
    }
    return http
        .csrf().disable()
        .authorizeExchange()
        .pathMatchers("/queue/**").permitAll()
        .pathMatchers("/test/**").permitAll()
        .pathMatchers("/user/login").permitAll()
        .pathMatchers("/user/register").permitAll()
        .anyExchange().authenticated()
        .and()
        .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .httpBasic().disable()
        .formLogin().disable()
        .logout().disable()
        .build()
  }
}