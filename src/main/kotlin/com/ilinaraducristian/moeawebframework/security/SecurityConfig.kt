package com.ilinaraducristian.moeawebframework.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: SecurityUserDetailsService,
    private val jwtRequestFilter: JwtRequestFilter
) : WebSecurityConfigurerAdapter() {

  override fun configure(auth: AuthenticationManagerBuilder) {
    auth.userDetailsService(userDetailsService)
  }

  override fun configure(http: HttpSecurity) {
    http
        .csrf().disable()
        .cors().and()
        .authorizeRequests()
        .antMatchers("/test/**").permitAll()
        .antMatchers("/queue/**").permitAll()
        .antMatchers("/user/login").permitAll()
        .antMatchers("/user/register").permitAll()
        .antMatchers("/public/getProblems").permitAll()
        .antMatchers("/public/getAlgorithms").permitAll()
        .antMatchers("/public/downloadProblem").permitAll()
        .antMatchers("/public/downloadAlgorithm").permitAll()
        .antMatchers("/public/getProblemsAndAlgorithms").permitAll()
        .antMatchers("/public/uploadProblem").hasRole("ADMIN")
        .antMatchers("/public/uploadAlgorithm").hasRole("ADMIN")
        .anyRequest().authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
  }

  @Bean
  override fun authenticationManagerBean(): AuthenticationManager {
    return super.authenticationManagerBean()
  }

}