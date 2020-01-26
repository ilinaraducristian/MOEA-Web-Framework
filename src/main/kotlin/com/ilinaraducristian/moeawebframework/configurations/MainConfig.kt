package com.ilinaraducristian.moeawebframework.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.security.FormAuthenticationSuccessHandler
import com.ilinaraducristian.moeawebframework.security.RestAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler

@Configuration
class MainConfig {

  @Bean
  fun jsonConverter(): ObjectMapper {
    return ObjectMapper()
  }

  @Bean
  fun encoder(): BCryptPasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean
  fun restAuthenticationEntryPoint(): RestAuthenticationEntryPoint {
    return RestAuthenticationEntryPoint()
  }

  @Bean
  fun formAuthenticationSuccessHandler(): FormAuthenticationSuccessHandler {
    return FormAuthenticationSuccessHandler()
  }

  @Bean
  fun simpleUrlAuthenticationFailureHandler(): SimpleUrlAuthenticationFailureHandler {
    return SimpleUrlAuthenticationFailureHandler()
  }

}