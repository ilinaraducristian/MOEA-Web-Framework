package org.moeawebframework.configs

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
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
            .pathMatchers(HttpMethod.GET, "/standards/algorithms").permitAll()
            .pathMatchers(HttpMethod.GET, "/standards/problems").permitAll()
            .pathMatchers(HttpMethod.GET, "/standards/referencesets").permitAll()
            .pathMatchers(HttpMethod.GET, "/evaluations/**").permitAll()
            .pathMatchers(HttpMethod.POST, "/evaluations").permitAll()
            .pathMatchers(HttpMethod.DELETE, "/evaluations/**").authenticated()
            .pathMatchers("/commonstructures/**").authenticated()
            .anyExchange().denyAll()
            .and()
            .oauth2ResourceServer().jwt().and()
            .and()
            .build()
    }
}