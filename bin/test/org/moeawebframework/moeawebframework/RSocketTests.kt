package org.moeawebframework.moeawebframework

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.moeawebframework.moeawebframework.configs.RSocketTestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.messaging.rsocket.RSocketRequester

@SpringBootTest
@Import(RSocketTestConfig::class)
class RSocketTests {

  @Autowired
  @Qualifier("getSuccessRSocketRequester")
  lateinit var rSocketRequester: RSocketRequester

//  @Autowired
//  @Qualifier("getFailedRSocketRequester")
//  lateinit var rSocketRequester: RSocketRequester

  @Test
  fun `simple test`() {
    assertDoesNotThrow {
      rSocketRequester.route("route").data("").retrieveMono(String::class.java).block()
    }
  }

}