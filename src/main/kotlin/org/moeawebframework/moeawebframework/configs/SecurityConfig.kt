package org.moeawebframework.moeawebframework.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerReactiveAuthenticationManagerResolver
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfig {

//  @Value("\${AUTHENTICATION_ISSUERS}")
//  lateinit var AUTHENTICATION_ISSUERS: List<String>

  /**
   * For authorities the default implementation uses scopes
   * instead of roles, a scope named "user" will match for
   * hasAuthority("SCOPE_user").
   * */
  @Bean
  fun springSecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    val issuers = listOf(
        "http://localhost:8180/auth/realms/MOEA-Web-Framework"
    )
    return http
        .csrf().disable()
        .authorizeExchange()
//        .pathMatchers("/queue/**").permitAll()
//        .pathMatchers("/test/**").permitAll()
//        .pathMatchers("/user/**").permitAll()
//        .pathMatchers("/user/signup").permitAll()
//        .pathMatchers("/user/login").permitAll()
//        .pathMatchers("/algorithm/**").permitAll()
//        .pathMatchers("/test/**").permitAll()
        .anyExchange().authenticated()
//        .anyExchange().permitAll()
        .and()
        .oauth2ResourceServer()
        .authenticationManagerResolver(JwtIssuerReactiveAuthenticationManagerResolver(issuers))
        .and()
        .build()
  }
}