package com.ilinaraducristian.moeawebframework.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

  @Bean
  fun reactiveRedisTemplate(jsonConverter: ObjectMapper, redisConnectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, QueueItem> {
    val keySerializer = StringRedisSerializer()
    val valueSerializer = Jackson2JsonRedisSerializer<QueueItem>(QueueItem::class.java)
    val builder = RedisSerializationContext.newSerializationContext<String, QueueItem>(keySerializer)
    val redisSerializationContext = builder.value(valueSerializer).build()
    return ReactiveRedisTemplate<String, QueueItem>(redisConnectionFactory, redisSerializationContext)
  }

}

