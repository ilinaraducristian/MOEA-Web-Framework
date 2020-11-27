package org.moeawebframework.moeawebframework

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.configs.redisType
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisAdapter(
    private val redisTemplate: ReactiveRedisTemplate<String, redisType>
) {

  suspend fun set(key: String, t: redisType): Boolean {
    return redisTemplate.opsForValue().set(key, t).awaitSingle()
  }

  suspend fun get(key: String): redisType? {
    return redisTemplate.opsForValue().get(key).awaitFirstOrNull()
  }

}