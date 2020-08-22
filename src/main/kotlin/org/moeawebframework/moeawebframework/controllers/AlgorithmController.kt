package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@RestController
@RequestMapping("algorithm")
class AlgorithmController(
    private val userService: UserService,
    private val userDAO: UserDAO
) {
  @PostMapping
  fun upload(authentication: Authentication, @RequestPart("data") filePart: FilePart, @RequestPart("name") name: String): Mono<String> {
    val principal = authentication.principal as Jwt
    val username = principal.claims["preferred_username"] as String
    return userDAO.getByUsername(username)
        .switchIfEmpty(Mono.error(RuntimeException(UserNotFoundException)))
        .flatMap {
          userService.uploadAlgorithm(it?.id!!, name, filePart)
        }
  }

  @GetMapping("{algorithm_sha256}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
  fun download(@PathVariable("algorithm_sha256") algorithm_sha256: String): Mono<ByteArray> {
    return WebClient.create("http://localhost:8070").get()
        .uri("""/$algorithm_sha256""")
        .exchange().flatMap {
//          it.headers().header("Content-Type")[0] = "application/octet-stream"
          it.bodyToMono(String::class.java)
        }.map {
          it.toByteArray()
        }
  }

  @DeleteMapping
  fun delete(authentication: Authentication?, algorithm_sha256: String): Mono<Unit> {
    return WebClient.create("http://localhost:8070").delete()
        .uri("""/$algorithm_sha256""")
        .exchange().map {}
  }
}