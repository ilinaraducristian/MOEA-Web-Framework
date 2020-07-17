package com.ilinaraducristian.moeawebframework.configurations

import com.ilinaraducristian.moeawebframework.entities.ProblemSolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisConfig {

  @Bean
  fun reactiveRedisTemplate(
      factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, ProblemSolver> {
    val keySerializer = StringRedisSerializer()
    val valueSerializer: Jackson2JsonRedisSerializer<ProblemSolver> = Jackson2JsonRedisSerializer<ProblemSolver>(ProblemSolver::class.java)
    val builder: RedisSerializationContextBuilder<String, ProblemSolver> = RedisSerializationContext.newSerializationContext<String, ProblemSolver>(keySerializer)
    val context: RedisSerializationContext<String, ProblemSolver> = builder.value(valueSerializer).build()
    return ReactiveRedisTemplate<String, ProblemSolver>(factory, context)
  }

}

