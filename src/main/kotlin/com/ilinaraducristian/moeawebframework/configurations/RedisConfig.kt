package com.ilinaraducristian.moeawebframework.configurations

import com.ilinaraducristian.moeawebframework.entities.QueueItem
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
      factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, QueueItem> {
    val keySerializer = StringRedisSerializer()
    val valueSerializer: Jackson2JsonRedisSerializer<QueueItem> = Jackson2JsonRedisSerializer<QueueItem>(QueueItem::class.java)
    val builder: RedisSerializationContextBuilder<String, QueueItem> = RedisSerializationContext.newSerializationContext<String, QueueItem>(keySerializer)
    val context: RedisSerializationContext<String, QueueItem> = builder.value(valueSerializer).build()
    return ReactiveRedisTemplate<String, QueueItem>(factory, context)
  }

}

