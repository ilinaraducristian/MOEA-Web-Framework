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
//        .pathMatchers("/v1/user/signup").permitAll()
//        .pathMatchers("/v1/user/login").permitAll()
//        .pathMatchers("/v1/test").permitAll()
//        .pathMatchers("/v1/queue").permitAll()
//        .pathMatchers("/v1/queue/process/**").permitAll()
        .anyExchange().permitAll()
        .and()
        .oauth2ResourceServer()
//        .authenticationManagerResolver(JwtIssuerReactiveAuthenticationManagerResolver(authentication_issuers))
        .jwt()
        .and()
        .and()
        .build()
  }
}