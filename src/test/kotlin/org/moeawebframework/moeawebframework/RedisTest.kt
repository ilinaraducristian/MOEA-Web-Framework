package org.moeawebframework.moeawebframework

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.moeawebframework.moeawebframework.configs.RSocketTestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.ReactiveRedisTemplate
import redis.embedded.RedisServer

@Import(RSocketTestConfig::class)
@SpringBootTest
class RedisTest {

  @Autowired
  lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

  companion object {

    private val redisServer = RedisServer(6370)

    @BeforeAll
    fun beforeAll() {
      redisServer.start()
    }

    @AfterAll
    fun afterAll() {
      redisServer.stop()
    }

  }

  @Test
  fun test() = runBlocking {
    redisTemplate.opsForValue().set("key", "value").awaitFirstOrNull()
    println(redisTemplate.opsForValue().get("key").awaitFirstOrNull())
  }

}