package org.moeawebframework.moeawebframework.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class MainConfig {

  @Bean
  fun encoder(): BCryptPasswordEncoder {
    return BCryptPasswordEncoder()
  }

}