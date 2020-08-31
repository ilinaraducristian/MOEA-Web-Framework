package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@RestController
@RequestMapping("problem")
class ProblemController(
    private val userService: UserService,
    private val userDAO: UserDAO
) {

  @Value("\${CDN_URI}")
  lateinit var CDN_URI: String

  @PostMapping
  fun upload(authentication: Authentication, @RequestPart("name") name: String, @RequestPart("problemData") problemFilePart: FilePart, @RequestPart("referenceSetData") referenceSetFilePart: FilePart): Mono<String> {
    val principal = authentication.principal as Jwt
    val username = principal.claims["preferred_username"] as String
    return userDAO.getByUsername(username)
        .switchIfEmpty(Mono.error(RuntimeException(UserNotFoundException)))
        .flatMap {
          userService.uploadProblem(it?.id!!, name, problemFilePart, referenceSetFilePart)
        }
  }

  @GetMapping("{problem_sha256}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
  fun download(@PathVariable("problem_sha256") problem_sha256: String): Mono<ByteArray> {
    return WebClient.create(CDN_URI).get()
        .uri("""/$problem_sha256""")
        .exchange().flatMap {
//          it.headers().header("Content-Type")[0] = "application/octet-stream"
          it.bodyToMono(String::class.java)
        }.map {
          it.toByteArray()
        }
  }

  @DeleteMapping
  fun delete(authentication: Authentication?, problem_sha256: String): Mono<Unit> {
    return WebClient.create(CDN_URI).delete()
        .uri("""/$problem_sha256""")
        .exchange().map {}
  }

}