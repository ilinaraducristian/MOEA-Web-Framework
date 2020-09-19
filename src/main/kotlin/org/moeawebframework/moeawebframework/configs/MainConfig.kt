package org.moeawebframework.moeawebframework.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.util.MimeTypeUtils
import java.security.MessageDigest

@Configuration
class MainConfig {

  @Value("\${rsocket_url}")
  lateinit var rsocket_url: String

  @Bean
  fun encoder(): BCryptPasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean
  fun hasher(): MessageDigest {
    return MessageDigest.getInstance("SHA-256")
  }

  @Bean
  fun rSocketRequester(): RSocketRequester {
    val values = rsocket_url.split(":")
    return RSocketRequester
        .builder()
        .rsocketStrategies(
            RSocketStrategies.builder()
                .encoder(Jackson2JsonEncoder())
                .decoder(Jackson2JsonDecoder())
                .build()
        )
        .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
        .connectTcp(values[0], Integer.parseInt(values[1]))
        .block()!!
  }

}