package org.moeawebframework.moeawebframework.security

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
class SecurityConfig {

  @Bean
  fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    return http
        .csrf().disable()
        .authorizeExchange()
        .pathMatchers("/queue/**").permitAll()
        .pathMatchers("/test/**").permitAll()
        .pathMatchers("/user/login").permitAll()
        .pathMatchers("/user/register").permitAll()
        .anyExchange().authenticated()
        .and()
        .addFilterAt(JwtAuthenticationWebFilter(JwtReactiveAuthenticationManager()), SecurityWebFiltersOrder.AUTHENTICATION)
        .httpBasic().disable()
        .formLogin().disable()
        .logout().disable()
        .build()
  }
}