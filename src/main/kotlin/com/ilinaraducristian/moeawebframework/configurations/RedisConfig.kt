package com.ilinaraducristian.moeawebframework.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.Problem
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import java.nio.ByteBuffer


@Configuration
class RedisConfig {

  @Bean
  @Primary
  fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory? {
    return LettuceConnectionFactory("localhost", 6379)
  }

  @Bean
  fun problemRedisTemplate(jsonConverter: ObjectMapper, connectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<Long, Problem> {
    val keySerializer = LongRedisSerializer()
    val valueSerializer = Jackson2JsonRedisSerializer<Problem>(Problem::class.java)
    val builder = RedisSerializationContext.newSerializationContext<Long, Problem>(keySerializer)
    val redisSerializationContext = builder.value(valueSerializer).build()
    return ReactiveRedisTemplate<Long, Problem>(connectionFactory, redisSerializationContext)
  }

  private class LongRedisSerializer : RedisSerializer<Long> {

    override fun serialize(l: Long?): ByteArray? {
      return if (l == null) EMPTY_BYTE_ARRAY else ByteBuffer.allocate(8)
          .putLong(0, l).array()
    }

    override fun deserialize(bytes: ByteArray?): Long? {
      return ByteBuffer.wrap(bytes).asLongBuffer().get(0)
    }

    companion object {
      private val EMPTY_BYTE_ARRAY: ByteArray = ByteBuffer.wrap(ByteArray(0))
          .array()
    }
  }

}

