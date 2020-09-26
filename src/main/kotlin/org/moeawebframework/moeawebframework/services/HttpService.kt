package org.moeawebframework.moeawebframework.services

import kotlinx.coroutines.reactive.awaitLast
import org.moeawebframework.moeawebframework.dto.AccessTokenDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.StreamingHttpOutputMessage
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class HttpService {
  
  @Value("\${cdn_url}")
  lateinit var cdn_url: String

  @Value("\${keycloak_url}")
  lateinit var keycloak_url: String

  @Value("\${client_secret}")
  lateinit var client_secret: String

  var keycloak_access_token: String? = null
  var keycloak_refresh_token: String? = null

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
    // do we have access token?
    // if yes
    // register user using access token
    // if registration failed with access denied use refresh token to get a new token
    // if no
    // get access token and refresh token
    if(keycloak_access_token == null) {
      adminLogin()
    }else {
      --header 'Content-Type: application/json' \
      --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFY2dlX3Y0c09fZUZ0TnhqYWJjT19QLTBhQ3p6S2VfMW02OU5mRjlBc1VzIn0.eyJleHAiOjE1OTI1Njc4OTEsImlhdCI6MTU5MjU2NzgzMSwianRpIjoiNjJiMWRlODEtNTBhMS00NzA2LWFmN2MtYzhhZTc1YTg1OTJhIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL21hc3RlciIsInN1YiI6IjNmYjc1YTM4LTA4NjctNGZlYi04ZTBlLWYzMTkxZTZlODZlMSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImFkbWluLWNsaSIsInNlc3Npb25fc3RhdGUiOiJhMDMwNGNiMS0xMzViLTQzODItYjYwMi0xNjNmNzgzYWNlN2IiLCJhY3IiOiIxIiwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbjIifQ.G9-OiyrGWk8cY4S3Ho255Y_euk_gTKDgYmGmU8RPBSeBNtFb_A68tuPFJxFKbzhZ1lipKJCXQsHbStoihvXAmmRsKzud5hDIvvnrD7CcVxAIpbd2wV5i6mB2wVLocV0_FCrE0-DNi_GPAKnazjFiVu3TxxM2L8Zsw7PHN9sb8Ux_lRvAFyNY5bT7NTbmEmt6LsO2An7iTZdBLScK9Lk9ZW8_0WG4eLMy9fatrpVV3MXhINW56gZD8WsWISY0m-cbIftDreZ1f2lzIjMGfkDrgCjy-VZjeIpbmffN-YGrUVywziymBRwA7FFLAxcf6jS5548HVxxKeMPIvNEfDG7eWw' \
      --data-raw '{"firstName":"Sergey","lastName":"Kargopolov", "email":"test@test.com", "enabled":"true", "username":"app-user"}'
      val multipartBodyBuilder = MultipartBodyBuilder()
      multipartBodyBuilder.part("username", signupInfoDTO.username)
      multipartBodyBuilder.part("password", signupInfoDTO.password)
      multipartBodyBuilder.part("first_name", signupInfoDTO.firstName)
      if (signupInfoDTO.lastName != null)
        multipartBodyBuilder.part("last_name", signupInfoDTO.lastName!!)
      multipartBodyBuilder.part("email", signupInfoDTO.email)
      var clientResponse = WebClient.create(keycloak_url).post()
          .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer $keycloak_access_token")
          .body(BodyInserters.fromValue(signupInfoDTO.toJSON())).exchange().awaitLast()
      if(clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
        adminLogin()
      }
    }
    multipartBodyBuilder.part("client_id", "admin-cli")
    multipartBodyBuilder.part("grant_type", "client_credentials")
    multipartBodyBuilder.part("client_secret", client_secret)
    return WebClient.create(keycloak_url).post().body(BodyInserters.fromMultipartData(multipartBodyBuilder.build())).exchange().awaitLast()
  }

  private suspend fun adminLogin() {
    val multipartBodyBuilder = MultipartBodyBuilder()
    if(keycloak_refresh_token != null) {
      multipartBodyBuilder.part("client_id", "admin-cli")
      multipartBodyBuilder.part("grant_type", "refresh_token")
      multipartBodyBuilder.part("refresh_token", keycloak_refresh_token!!)
      WebClient.create(keycloak_url).post()
          .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
          .exchange()
    }else {

    }
    multipartBodyBuilder.part("client_id", "admin-cli")
    multipartBodyBuilder.part("grant_type", "client_credentials")
    multipartBodyBuilder.part("client_secret", client_secret)
    val bar = WebClient.create(keycloak_url).post().body(BodyInserters.fromMultipartData(multipartBodyBuilder.build())).exchange().awaitLast()
    val accessTokenDTO = bar.awaitBody<AccessTokenDTO>()
    keycloak_access_token = accessTokenDTO.access_token
    keycloak_refresh_token = accessTokenDTO.refresh_token
  }

}