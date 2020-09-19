package org.moeawebframework.moeawebframework.services

import kotlinx.coroutines.reactive.awaitLast
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient

@Service
class HttpService {
  
  @Value("\${cdn_url}")
  lateinit var cdn_url: String

  @Value("\${keycloak_signup_url}")
  lateinit var keycloak_signup_url: String

  @Value("\${keycloak_login_url}")
  lateinit var keycloak_login_url: String

  suspend fun getFromCDN(sha256: String): ClientResponse {
    return WebClient.create("""${cdn_url}/$sha256""").get().exchange().awaitLast()
  }

  suspend fun deleteFromCDN(sha256: String): ClientResponse {
    return WebClient.create("""${cdn_url}/$sha256""").delete().exchange().awaitLast()
  }

  suspend fun uploadToCDN(body: BodyInserters.MultipartInserter): ClientResponse {
    return WebClient.create(cdn_url).post().body(body).exchange().awaitLast()
  }

  suspend fun keycloakSignup(body: BodyInserters.MultipartInserter): ClientResponse {
    return WebClient.create(keycloak_signup_url).post().body(body).exchange().awaitLast()
  }

  suspend fun keycloakLogin(body: BodyInserters.MultipartInserter): ClientResponse {
    return WebClient.create(keycloak_login_url).post().body(body).exchange().awaitLast()
  }

}