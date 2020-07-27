package org.moeawebframework.moeawebframework.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfig {

  @Bean
  fun springSecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    return http
        .csrf().disable()
        .authorizeExchange()
        .pathMatchers("/queue/**").permitAll()
        .pathMatchers("/test/**").permitAll()
        .pathMatchers("/user/login").permitAll()
        .pathMatchers("/user/signup").permitAll()
        .anyExchange().authenticated()
        .and()
        .addFilterAt(JwtAuthenticationWebFilter(JwtReactiveAuthenticationManager()), SecurityWebFiltersOrder.AUTHENTICATION)
        .httpBasic().disable()
        .formLogin().disable()
        .logout().disable()
        .build()
  }
}