package com.ilinaraducristian.moeawebframework.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@TestConfiguration
class TestRedisConfiguration(
    @Value("\${spring.session.test-port}") port: Int
) {
  private val redisServer: RedisServer = RedisServer(port)

  @PostConstruct
  fun postConstruct() {
    redisServer.start()
  }

  @PreDestroy
  fun preDestroy() {
    redisServer.stop()
  }

}