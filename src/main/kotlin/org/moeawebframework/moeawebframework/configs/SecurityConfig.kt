package org.moeawebframework.moeawebframework.configs

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.config.WebFluxConfigurer
import java.util.*


@EnableWebFluxSecurity
class SecurityConfig{

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
        .pathMatchers("/public/**").permitAll()
        .pathMatchers("/user/**").authenticated()
        .pathMatchers(HttpMethod.OPTIONS, "/asd/**").permitAll()
        .pathMatchers("/asd/**").authenticated()
        .anyExchange().denyAll()
        .and()
        .oauth2ResourceServer().jwt().and()
        .and()
        .build()
  }

}