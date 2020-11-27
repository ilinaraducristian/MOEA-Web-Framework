package org.moeawebframework.moeawebframework.configs

import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.moeawebframework.moeawebframework.RedisAdapter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class RedisTestConfig {

  private val mockRedisDB = HashMap<String, redisType>()

  @Bean
  fun mockRedisAdapter(): RedisAdapter {
    val redisAdapter = Mockito.mock(RedisAdapter::class.java)
    runBlocking {
      Mockito.`when`(redisAdapter.set(Mockito.anyString(), Mockito.any(redisType::class.java)))
          .then {
            mockRedisDB[it.getArgument(0, String::class.java)] = it.getArgument(1, redisType::class.java)
            return@then true
          }
      Mockito.`when`(redisAdapter.get(Mockito.anyString()))
          .then {
            return@then mockRedisDB[it.getArgument(0, String::class.java)]
          }
      return@runBlocking
    }
    return redisAdapter
  }

}