package org.moeawebframework.moeawebframework.controllers

import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.moeawebframework.moeawebframework.dto.QueueItemResponseDTO
import org.moeawebframework.moeawebframework.exceptions.QueueItemNotFoundException
import org.moeawebframework.moeawebframework.services.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("user/queue")
class UserQueueController(
    private val userService: UserService
) {

  @PostMapping
  suspend fun addQueueItem(authentication: Authentication, @Valid @RequestBody queueItemDTO: QueueItemDTO): String {
    val principal = authentication.principal as Jwt
    return userService.addQueueItem(principal.getClaim("sub"), queueItemDTO)
  }

  @GetMapping("{rabbitId}")
  suspend fun getQueueItem(authentication: Authentication, @PathVariable rabbitId: String): QueueItemResponseDTO {
    val principal = authentication.principal as Jwt
    val queueItem = userService.getQueueItem(principal.getClaim("sub"), rabbitId)
        ?: throw RuntimeException(QueueItemNotFoundException)
    return QueueItemResponseDTO(queueItem)
  }

  @PostMapping("{rabbitId}")
  suspend fun startQueueItemProcessing(authentication: Authentication, @PathVariable rabbitId: String) {
    val principal = authentication.principal as Jwt
    userService.startProcessing(principal.getClaim("sub"), rabbitId)
  }

  @PostMapping("cancel/{rabbitId}")
  suspend fun cancelQueueItemProcessing(authentication: Authentication, @PathVariable rabbitId: String) {
    val principal = authentication.principal as Jwt
    userService.cancelProcessing(principal.getClaim("sub"), rabbitId)
  }

  @DeleteMapping("{rabbitId}")
  suspend fun deleteQueueItem(authentication: Authentication, @PathVariable rabbitId: String) {
    val principal = authentication.principal as Jwt
    userService.deleteQueueItem(principal.getClaim("sub"), rabbitId)
  }

}