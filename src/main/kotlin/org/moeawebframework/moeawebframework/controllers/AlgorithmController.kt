package org.moeawebframework.moeawebframework.controllers

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.moeawebframework.moeawebframework.services.HttpService
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.awaitBody

@RestController
@RequestMapping("algorithm")
class AlgorithmController(
    private val userService: UserService,
    private val userDAO: UserDAO,
    private val httpService: HttpService
) {

  @PostMapping
  suspend fun upload(authentication: Authentication, @RequestPart("data") filePart: FilePart, @RequestPart("name") name: String): String {
    val principal = authentication.principal as Jwt
    val username = principal.claims["preferred_username"] as String
    val user = userDAO.getByUsername(username).awaitFirstOrNull() ?: throw RuntimeException(UserNotFoundException)
    return userService.uploadAlgorithm(user.id!!, name, filePart)
  }

  @GetMapping("{algorithm_sha256}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
  suspend fun download(@PathVariable("algorithm_sha256") algorithm_sha256: String): ByteArray {
    val response = httpService.getFromCDN(algorithm_sha256)
    return response.awaitBody()
  }

  @DeleteMapping
  suspend fun delete(authentication: Authentication?, algorithm_sha256: String) {
    httpService.deleteFromCDN(algorithm_sha256)
  }
}