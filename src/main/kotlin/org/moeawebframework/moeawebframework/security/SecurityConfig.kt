package org.moeawebframework.moeawebframework.security

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
class SecurityConfig {

  @Bean
  fun springSecurityFilterChain(
      http: ServerHttpSecurity): SecurityWebFilterChain {
    http.csrf().disable()
        .authorizeExchange()
//        .pathMatchers(HttpMethod.POST, "/employees/update").hasRole("ADMIN")
        .pathMatchers("/**").permitAll()
        .and()
        .httpBasic()
    return http.build()
  }
}