package org.moeawebframework.moeawebframework

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.ReactiveRedisTemplate

@SpringBootTest
@Import(TestConfig::class)
class RedisTest {

  @Autowired
  lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

  @Test
  fun test() = runBlocking {
    redisTemplate.opsForValue().set("key", "value").awaitFirstOrNull()
    println(redisTemplate.opsForValue().get("key").awaitFirstOrNull())
  }

}