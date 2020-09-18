package org.moeawebframework.moeawebframework.controllers

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import org.moeawebframework.moeawebframework.configs.MainConfig
import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.exceptions.ProblemNotFoundException
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("problem")
class ProblemController(
    private val userService: UserService,
    private val userDAO: UserDAO
) {

  @PostMapping
  suspend fun upload(authentication: Authentication, @RequestPart("name") name: String, @RequestPart("problemData") problemFilePart: FilePart, @RequestPart("referenceSetData") referenceSetFilePart: FilePart): String {
    val principal = authentication.principal as Jwt
    val username = principal.claims["preferred_username"] as String
    val user = userDAO.getByUsername(username).awaitFirstOrNull() ?: throw RuntimeException(UserNotFoundException)
    return userService.uploadProblem(user.id!!, name, problemFilePart, referenceSetFilePart).awaitLast()
  }

  @GetMapping("{problem_sha256}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
  suspend fun download(@PathVariable("problem_sha256") problem_sha256: String): ByteArray {
    val response = MainConfig.getFromCDN(problem_sha256).awaitFirstOrNull()
        ?: throw RuntimeException(ProblemNotFoundException)
    return response.bodyToMono(ByteArray::class.java).awaitLast()
  }

  @DeleteMapping
  suspend fun delete(authentication: Authentication?, problem_sha256: String) {
    MainConfig.deleteFromCDN(problem_sha256).awaitFirstOrNull()
  }

}