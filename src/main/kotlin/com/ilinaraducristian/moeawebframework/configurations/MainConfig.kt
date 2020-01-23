package com.ilinaraducristian.moeawebframework.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MainConfig {

  @Bean
  fun jsonConverter(): ObjectMapper {
    return ObjectMapper()
  }

}