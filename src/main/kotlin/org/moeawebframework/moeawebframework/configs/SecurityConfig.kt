package org.moeawebframework.moeawebframework.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfig {

  /**
   * For authorities the default implementation uses scopes
   * instead of roles, a scope named "user" will match for
   * hasAuthority("SCOPE_user").
   * */
  @Bean
  fun springSecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    return http
        .csrf().disable()
        .authorizeExchange()
        .pathMatchers("public/**").permitAll()
        .pathMatchers("user/**").authenticated()
        .anyExchange().denyAll()
        .and()
        .oauth2ResourceServer().jwt().and()
        .and()
        .build()
  }
}