package org.moeawebframework.moeawebframework.configs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerReactiveAuthenticationManagerResolver
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfig {

  lateinit var authentication_issuers: List<String>

  @Autowired
  fun setAuthentication_issuers(@Value("\${authentication_issuers}") authenticationIssuers: String) {
    authentication_issuers = authenticationIssuers.split(",")
  }

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
        .pathMatchers("/user/signup").permitAll()
        .pathMatchers("/user/login").permitAll()
        .pathMatchers("/test").permitAll()
        .pathMatchers("/queue").permitAll()
        .pathMatchers("/queue/process/**").permitAll()
//        .pathMatchers("/test/**").permitAll()
//        .pathMatchers("/user/**").permitAll()
//        .pathMatchers("/user/signup").permitAll()
//        .pathMatchers("/user/login").permitAll()
//        .pathMatchers("/algorithm/**").permitAll()
//        .pathMatchers("/test/**").permitAll()
//        .anyExchange().authenticated()
        .anyExchange().denyAll()
        .and()
        .oauth2ResourceServer()
        .authenticationManagerResolver(JwtIssuerReactiveAuthenticationManagerResolver(authentication_issuers))
        .and()
        .build()
  }
}