package org.moeawebframework.moeawebframework

import org.moeawebframework.moeawebframework.configs.redisType
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.deleteAndAwait
import org.springframework.data.redis.core.getAndAwait
import org.springframework.data.redis.core.setAndAwait
import org.springframework.stereotype.Component

@Component
class RedisAdapter(
    private val redisTemplate: ReactiveRedisTemplate<String, redisType>
) {

  suspend fun set(key: String, t: redisType): Boolean {
    return redisTemplate.opsForValue().setAndAwait(key, t)
  }

  suspend fun get(key: String): redisType? {
    return redisTemplate.opsForValue().getAndAwait(key)
  }

  suspend fun delete(key: String): Long {
    return redisTemplate.deleteAndAwait(key)
  }

}