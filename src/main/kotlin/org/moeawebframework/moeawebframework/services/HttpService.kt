package org.moeawebframework.moeawebframework.services

import kotlinx.coroutines.reactive.awaitLast
import org.moeawebframework.moeawebframework.dto.KeycloakTokenDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.exceptions.UsernameTakenException
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
  private lateinit var cdn_url: String

  @Value("\${keycloak.auth.url}")
  private lateinit var keycloak_auth_url: String

  @Value("\${keycloak.auth.client}")
  private lateinit var keycloak_auth_client: String

  @Value("\${keycloak.admin.url}")
  private lateinit var keycloak_admin_url: String

  @Value("\${keycloak.admin.client}")
  private lateinit var keycloak_admin_client: String

  @Value("\${keycloak.admin.secret}")
  private lateinit var keycloak_admin_secret: String

  private var keycloakTokenDTO: KeycloakTokenDTO? = null

  fun mustErrorFcn() {
    throw RuntimeException(UsernameTakenException)
  }

  suspend fun getFromCDN(sha256: String): ClientResponse {
    return WebClient.create("$cdn_url/$sha256").get().exchange().awaitLast()
  }

  suspend fun deleteFromCDN(sha256: String): ClientResponse {
    return WebClient.create("$cdn_url/$sha256").delete().exchange().awaitLast()
  }

  suspend fun uploadToCDN(body: BodyInserters.MultipartInserter): ClientResponse {
    return WebClient.create(cdn_url).post().body(body).exchange().awaitLast()
  }

  suspend fun keycloakSignup(signupInfoDTO: SignupInfoDTO): ClientResponse {
    if (keycloakTokenDTO == null) {
      adminLogin()
    }
    var response = signup(signupInfoDTO)
    if (response.statusCode() != HttpStatus.CREATED) {
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

  suspend fun keycloakFindByEmail(email: String): ClientResponse {
    if (keycloakTokenDTO == null) {
      adminLogin()
    }
    var response = findByEmail(email)
    if (response.statusCode() != HttpStatus.OK) {
      adminLogin()
      response = findByEmail(email)
    }
    return response
  }

  suspend fun keycloakDelete(id: String): ClientResponse {
    if (keycloakTokenDTO == null) {
      adminLogin()
    }
    var response = delete(id)
    if (response.statusCode() != HttpStatus.NO_CONTENT) {
      adminLogin()
      response = delete(id)
    }
    return response
  }

  private suspend fun adminLogin() {
    if (keycloakTokenDTO == null) {
      val response = login(false)
      keycloakTokenDTO = response.awaitBody()
      return
    }
    var response = login(true)
    if (response.statusCode() != HttpStatus.OK) {
      response = login(false)
    }
    keycloakTokenDTO = response.awaitBody()

  }

  private suspend fun signup(signupInfoDTO: SignupInfoDTO): ClientResponse {
    return WebClient.create(keycloak_admin_url)
        .post()
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer ${keycloakTokenDTO?.access_token}")
        .bodyValue(signupInfoDTO.toKeycloakCredentialRepresentation())
        .awaitExchange()
  }

  private suspend fun login(hasRefreshToken: Boolean): ClientResponse {
    val bodyInserter = BodyInserters.fromFormData("client_id", keycloak_admin_client)
        .with("client_secret", keycloak_admin_secret)
    if (hasRefreshToken) {
      bodyInserter.with("grant_type", "refresh_token")
      bodyInserter.with("refresh_token", keycloakTokenDTO!!.refresh_token)
    } else {
      bodyInserter.with("grant_type", "client_credentials")
    }
    return WebClient.create(keycloak_auth_url)
        .post()
        .body(bodyInserter)
        .awaitExchange()
  }

  private suspend fun findByEmail(email: String): ClientResponse {
    return WebClient.create("${keycloak_admin_url}/?email=$email")
        .get()
        .header("Authorization", "Bearer ${keycloakTokenDTO?.access_token}")
        .awaitExchange()
  }

  private suspend fun delete(id: String): ClientResponse {
    return WebClient.create("${keycloak_admin_url}/$id")
        .delete()
        .header("Authorization", "Bearer ${keycloakTokenDTO?.access_token}")
        .awaitExchange()
  }

}