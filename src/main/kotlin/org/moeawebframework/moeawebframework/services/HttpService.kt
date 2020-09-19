package org.moeawebframework.moeawebframework.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class HttpService {
  
  @Value("\${cdn_url}")
  lateinit var cdn_url: String

  @Value("\${keycloak_signup_url}")
  lateinit var keycloak_signup_url: String

  @Value("\${keycloak_login_url}")
  lateinit var keycloak_login_url: String

  fun getFromCDN(sha256: String): Mono<ClientResponse> {
    return WebClient.create("""${cdn_url}/$sha256""").get().exchange()
  }

  fun deleteFromCDN(sha256: String): Mono<ClientResponse> {
    return WebClient.create("""${cdn_url}/$sha256""").delete().exchange()
  }

  fun uploadToCDN(body: BodyInserters.MultipartInserter): Mono<ClientResponse> {
    return WebClient.create(cdn_url).post().body(body).exchange()
  }

  fun keycloakSignup(body: BodyInserters.MultipartInserter): Mono<ClientResponse> {
    return WebClient.create(keycloak_signup_url).post().body(body).exchange()
  }

  fun keycloakLogin(body: BodyInserters.MultipartInserter): Mono<ClientResponse> {
    return WebClient.create(keycloak_login_url).post().body(body).exchange()
  }

}