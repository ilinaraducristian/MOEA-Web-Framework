package com.ilinaraducristian.moeawebframework.configurations

import com.ilinaraducristian.moeawebframework.security.FormAuthenticationSuccessHandler
import com.ilinaraducristian.moeawebframework.security.RestAuthenticationEntryPoint
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
//    private val encoder: BCryptPasswordEncoder,
    private val restAuthenticationEntryPoint: RestAuthenticationEntryPoint,
    private val formAuthenticationSuccessHandler: FormAuthenticationSuccessHandler,
    private val simpleUrlAuthenticationFailureHandler: SimpleUrlAuthenticationFailureHandler,
    private val dataSource: DataSource
) : WebSecurityConfigurerAdapter() {

  override fun configure(auth: AuthenticationManagerBuilder) {
//    auth
//        .inMemoryAuthentication()
//        .withUser("user")
//        .password(encoder.encode("password"))
//        .roles("USER")
    auth.jdbcAuthentication().dataSource(dataSource)
  }

  override fun configure(http: HttpSecurity) {
    http
        .csrf().disable()
        .exceptionHandling()
        .authenticationEntryPoint(restAuthenticationEntryPoint)
        .and()
        .authorizeRequests()
        .antMatchers("/test").permitAll()
        .antMatchers("/queue").permitAll()
        .antMatchers("/algorithm").authenticated()
        .antMatchers("/problem").authenticated()
        .antMatchers("/user").authenticated()
        .antMatchers("/admin").hasRole("ADMIN")
        .and()
        .formLogin()
        .successHandler(formAuthenticationSuccessHandler)
        .failureHandler(simpleUrlAuthenticationFailureHandler)
        .and()
        .logout()
        .logoutSuccessHandler(HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
  }

}