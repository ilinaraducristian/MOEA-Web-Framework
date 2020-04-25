package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmExistsOnServerException
import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmNotFoundOnServerException
import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Files
import java.security.Principal
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("algorithm")
class AlgorithmController(
    private val userRepo: UserRepository
) {

  @PutMapping("upload")
  fun upload(@RequestParam("algorithm") file: MultipartFile, @RequestParam("override") override: Boolean, @RequestParam("name") name: String, principal: Principal): Mono<Unit> {
    return mono {
      val existingFile = File("moeaData/${principal.name}/algorithms/${file.originalFilename}.class")
      if (existingFile.exists() && !override) {
        throw AlgorithmExistsOnServerException()
      }
      val user = userRepo.findByUsername(principal.name) ?: throw UserNotFoundException()
      user.algorithms.add(file.originalFilename.toString().replace("""\.class""", ""))
      userRepo.save(user)
      withContext(Dispatchers.IO) {
        file.transferTo(File("moeaData/${principal.name}/algorithms/${file.originalFilename}"))
      }
    }
  }

  @DeleteMapping("{name}")
  fun delete(@PathVariable name: String, principal: Principal): Mono<Unit> {
    return mono {
      val user = userRepo.findByUsername(principal.name) ?: throw UserNotFoundException()
      if (!user.algorithms.contains(name)) {
        throw AlgorithmNotFoundException()
      }
      val file = File("moeaData/${principal.name}/algorithms/$name.class")
      file.delete()
      user.algorithms.remove(name)
      userRepo.save(user)
      return@mono
    }
  }

  @GetMapping("/{name}")
  fun download(request: HttpServletRequest, response: HttpServletResponse, @PathVariable name: String, principal: Principal): Mono<Unit> {
    return mono {
      val user = userRepo.findByUsername(principal.name) ?: throw UserNotFoundException()
      val file = File("moeaData/${user.username}/algorithms/$name.class")
      if (!file.exists())
        throw AlgorithmNotFoundOnServerException()
      response.contentType = "application/octet-stream"
      response.addHeader("Content-Disposition", "attachment; filename=$name.class")
      withContext(Dispatchers.IO) {
        Files.copy(file.toPath(), response.outputStream)
        response.outputStream.flush()
      }
    }
  }

}