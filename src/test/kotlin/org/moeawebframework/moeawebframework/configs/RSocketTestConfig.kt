package org.moeawebframework.moeawebframework.configs

import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.ParameterizedTypeReference
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Consumer

@TestConfiguration
class RSocketTestConfig {

  @Bean
  fun rSocketRequester(): RSocketRequester {
    return getSuccessRSocketRequester()
  }

  @Bean
  fun getSuccessRSocketRequester(): RSocketRequester {
    val rSocketRequester = Mockito.mock(RSocketRequester::class.java)
    Mockito.`when`(rSocketRequester.route(Mockito.anyString(), Mockito.any()))
        .then {
          SuccessfullTestRequestSpec(it.arguments[0] as String)
        }
    return rSocketRequester
  }

  @Bean
  fun getFailedRSocketRequester(): RSocketRequester {
    val rSocketRequester = Mockito.mock(RSocketRequester::class.java)
    Mockito.`when`(rSocketRequester.route(Mockito.anyString(), Mockito.any()))
        .then {
          FailedTestRequestSpec(it.arguments[0] as String)
        }
    return rSocketRequester
  }

}

class SuccessfullTestRequestSpec(private val route: String) : RSocketRequester.RequestSpec {
  override fun metadata(p0: Consumer<RSocketRequester.MetadataSpec<*>>): RSocketRequester.RequestSpec {
    return this
  }

  override fun metadata(p0: Any, p1: org.springframework.util.MimeType): RSocketRequester.RequestSpec {
    return this
  }

  override fun <T : Any?> retrieveMono(p0: Class<T>): Mono<T> {
    return Mono.empty()
  }

  override fun <T : Any?> retrieveMono(p0: ParameterizedTypeReference<T>): Mono<T> {
    return Mono.empty()
  }

  override fun <T : Any?> retrieveFlux(p0: Class<T>): Flux<T> {
    return Flux.empty()
  }

  override fun <T : Any?> retrieveFlux(p0: ParameterizedTypeReference<T>): Flux<T> {
    return Flux.empty()
  }

  override fun data(p0: Any): RSocketRequester.RetrieveSpec {
    return this
  }

  override fun data(p0: Any, p1: Class<*>): RSocketRequester.RetrieveSpec {
    return this
  }

  override fun data(p0: Any, p1: ParameterizedTypeReference<*>): RSocketRequester.RetrieveSpec {
    return this
  }

  override fun send(): Mono<Void> {
    return Mono.empty()
  }

}

class FailedTestRequestSpec(private val route: String) : RSocketRequester.RequestSpec {
  override fun metadata(p0: Consumer<RSocketRequester.MetadataSpec<*>>): RSocketRequester.RequestSpec {
    return this
  }

  override fun metadata(p0: Any, p1: org.springframework.util.MimeType): RSocketRequester.RequestSpec {
    return this
  }

  override fun <T : Any?> retrieveMono(p0: Class<T>): Mono<T> {
    return Mono.error(RuntimeException("Something went wrong"))
  }

  override fun <T : Any?> retrieveMono(p0: ParameterizedTypeReference<T>): Mono<T> {
    return Mono.empty()
  }

  override fun <T : Any?> retrieveFlux(p0: Class<T>): Flux<T> {
    return Flux.empty()
  }

  override fun <T : Any?> retrieveFlux(p0: ParameterizedTypeReference<T>): Flux<T> {
    return Flux.empty()
  }

  override fun data(p0: Any): RSocketRequester.RetrieveSpec {
    return this
  }

  override fun data(p0: Any, p1: Class<*>): RSocketRequester.RetrieveSpec {
    return this
  }

  override fun data(p0: Any, p1: ParameterizedTypeReference<*>): RSocketRequester.RetrieveSpec {
    return this
  }

  override fun send(): Mono<Void> {
    return Mono.empty()
  }

}