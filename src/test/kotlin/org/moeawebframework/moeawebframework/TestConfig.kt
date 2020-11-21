package org.moeawebframework.moeawebframework

import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.core.ParameterizedTypeReference
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Consumer

@Configuration
@Profile("test")
class TestConfig {

  @Bean
  @Primary
  fun getRSocketRequester(): RSocketRequester {
    val rSocketRequester = Mockito.mock(RSocketRequester::class.java)
    Mockito.`when`(rSocketRequester.route(Mockito.anyString(), Mockito.any()))
        .then {
          TestRequestSpec(it.arguments[0] as String)
        }
    return rSocketRequester
  }

}

class TestRequestSpec(private val route: String) : RSocketRequester.RequestSpec {
  override fun metadata(p0: Consumer<RSocketRequester.MetadataSpec<*>>): RSocketRequester.RequestSpec {
    TODO("Not yet implemented")
  }

  override fun metadata(p0: Any, p1: org.springframework.util.MimeType): RSocketRequester.RequestSpec {
    TODO("Not yet implemented")
  }

  override fun <T : Any?> retrieveMono(p0: Class<T>): Mono<T> {
    if(route == "startProcessing") {
      return Mono.empty()
    }else if(route == "cancelProcessing"){
      return Mono.empty()
    }
    return Mono.just("String" as T)
  }

  override fun <T : Any?> retrieveMono(p0: ParameterizedTypeReference<T>): Mono<T> {
    if(route == "startProcessing") {
      return Mono.empty()
    }else if(route == "cancelProcessing"){
      return Mono.empty()
    }
    return Mono.just("String" as T)
  }

  override fun <T : Any?> retrieveFlux(p0: Class<T>): Flux<T> {
    TODO("Not yet implemented")
  }

  override fun <T : Any?> retrieveFlux(p0: ParameterizedTypeReference<T>): Flux<T> {
    TODO("Not yet implemented")
  }

  override fun data(p0: Any): RSocketRequester.RetrieveSpec {
      return this
  }

  override fun data(p0: Any, p1: Class<*>): RSocketRequester.RetrieveSpec {
    return this
  }

  override fun data(p0: Any, p1: ParameterizedTypeReference<*>): RSocketRequester.RetrieveSpec {
    TODO("Not yet implemented")
  }

  override fun send(): Mono<Void> {
    TODO("Not yet implemented")
  }

}