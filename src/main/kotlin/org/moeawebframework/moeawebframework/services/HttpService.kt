package org.moeawebframework.moeawebframework.services

import kotlinx.coroutines.reactive.awaitLast
import org.moeawebframework.moeawebframework.dto.AccessTokenDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange

@Service
class HttpService {

  @Value("\${cdn_url}")
  lateinit var cdn_url: String

  @Value("\${keycloak.auth.url}")
  lateinit var keycloak_auth_url: String

  @Value("\${keycloak.auth.client}")
  lateinit var keycloak_auth_client: String

  @Value("\${keycloak.admin.url}")
  lateinit var keycloak_admin_url: String

  @Value("\${keycloak.admin.client}")
  lateinit var keycloak_admin_client: String

  @Value("\${keycloak.admin.secret}")
  lateinit var keycloak_admin_secret: String

  private var accessTokenDTO: AccessTokenDTO? = null

  suspend fun getFromCDN(sha256: String): ClientResponse {
    return WebClient.create("""${cdn_url}/$sha256""").get().exchange().awaitLast()
  }

  suspend fun deleteFromCDN(sha256: String): ClientResponse {
    return WebClient.create("""${cdn_url}/$sha256""").delete().exchange().awaitLast()
  }

  suspend fun uploadToCDN(body: BodyInserters.MultipartInserter): ClientResponse {
    return WebClient.create(cdn_url).post().body(body).exchange().awaitLast()
  }

  suspend fun keycloakRegister(signupInfoDTO: SignupInfoDTO): ClientResponse {
    if (accessTokenDTO == null) {
      adminLogin()
    }
    var response = signup(signupInfoDTO)
    if (response.statusCode() != HttpStatus.OK) {
      adminLogin()
      response = signup(signupInfoDTO)
    }
    return response
  }

  suspend fun keycloakLogin(userCredentialsDTO: UserCredentialsDTO): ClientResponse {
    return WebClient.create(keycloak_auth_url)
        .post()
        .body(BodyInserters.fromFormData("client_id", keycloak_auth_client)
            .with("grant_type", "password")
            .with("username", userCredentialsDTO.username)
            .with("password", userCredentialsDTO.password))
        .awaitExchange()
  }

  private suspend fun adminLogin() {
    if (accessTokenDTO == null) {
      val response = login(false)
      accessTokenDTO = response.awaitBody()
      return
    }
    var response = login(true)
    if (response.statusCode() != HttpStatus.OK) {
      response = login(false)
    }
    accessTokenDTO = response.awaitBody()

  }

  private suspend fun signup(signupInfoDTO: SignupInfoDTO): ClientResponse {
    return WebClient.create(keycloak_admin_url)
        .post()
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer ${accessTokenDTO?.access_token}")
        .bodyValue(signupInfoDTO.toJSON())
        .awaitExchange()
  }

  private suspend fun login(hasRefreshToken: Boolean): ClientResponse {
    val bodyInserter = BodyInserters.fromFormData("client_id", keycloak_admin_client)
        .with("client_secret", keycloak_admin_secret)
    if (hasRefreshToken) {
      bodyInserter.with("grant_type", "refresh_token")
      bodyInserter.with("refresh_token", accessTokenDTO!!.refresh_token)
    }else {
      bodyInserter.with("grant_type", "client_credentials")
    }
    return WebClient.create(keycloak_auth_url)
        .post()
        .body(bodyInserter)
        .awaitExchange()
  }

}