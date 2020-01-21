package com.ilinaraducristian.moeawebframework.configurations

import com.ilinaraducristian.moeawebframework.dto.Problem
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisConfig {

  @Bean
  fun problemRedisConnection(): LettuceConnectionFactory {
    return LettuceConnectionFactory(RedisStandaloneConfiguration("localhost", 6379))
  }

  @Bean
  fun demoRedisTemplate(): RedisTemplate<Long, Problem> {
    val template = RedisTemplate<Long, Problem>()
    template.setConnectionFactory(problemRedisConnection())
    return template
  }

}