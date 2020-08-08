package org.moeawebframework.moeawebframework.configs

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.util.MimeTypeUtils
import java.security.MessageDigest
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class MainConfig {

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  fun encoder(): BCryptPasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  fun hasher(): MessageDigest {
    return MessageDigest.getInstance("SHA-256")
  }

  @Bean
  fun rSocketRequester(): RSocketRequester {
    return RSocketRequester
        .builder()
        .rsocketStrategies(
            RSocketStrategies.builder()
                .encoder(Jackson2JsonEncoder())
                .decoder(Jackson2JsonDecoder())
                .build()
        )
        .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
        .connectTcp("localhost", 7000)
        .block()!!
  }

}