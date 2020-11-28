package org.moeawebframework.moeawebframework

import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.moeawebframework.moeawebframework.services.PublicService
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class PublicServiceTestConfig {

  private fun <T> any(): T {
    return Mockito.any<T>()
  }

  @Bean
  fun mockPublicService(): PublicService {
    val publicService = Mockito.mock(PublicService::class.java)
    runBlocking {
      Mockito.`when`(publicService.addQueueItem(any()))
          .thenReturn("randomUUID")
    }
    return publicService
  }

}