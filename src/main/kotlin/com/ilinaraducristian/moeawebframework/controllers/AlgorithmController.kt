package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.entities.Algorithm
import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmExistsOnServerException
import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmNotFoundOnServerException
import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
import com.ilinaraducristian.moeawebframework.repositories.AlgorithmRepository
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
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
    private val userRepo: UserRepository,
    private val algorithmRepo: AlgorithmRepository
) {

  @PutMapping("upload")
  fun upload(@RequestParam("file") file: MultipartFile, @RequestParam("override") override: Boolean, @RequestParam("name") name: String, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val existingFile = File("moeaData/${principal.name}/algorithms/${file.originalFilename}.class")
      if (existingFile.exists() && !override) {
        return@create it.error(AlgorithmExistsOnServerException())
      }
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty) {
        return@create it.error(UserNotFoundException())
      }
      val user = foundUser.get()
      val algorithm = Algorithm()
      algorithm.name = file.originalFilename.toString().replace(Regex("""\.class"""), "")
      user.algorithms.add(algorithm)
      algorithm.users.add(user)
      userRepo.save(user)
      file.transferTo(File("moeaData/${principal.name}/algorithms/${file.originalFilename}.class"))
      it.success()
    }
  }

  @DeleteMapping("{name}")
  fun delete(@PathVariable name: String, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty) {
        return@create it.error(UserNotFoundException())
      }
      val user = foundUser.get()
      val foundAlgorithm = algorithmRepo.findByUsersAndName(user, name)
      if (foundAlgorithm.isEmpty) {
        return@create it.error(AlgorithmNotFoundException())
      }
      val file = File("moeaData/${principal.name}/algorithms/$name.class")
      if (!file.exists()) {
        return@create it.error(AlgorithmNotFoundOnServerException())
      }
      algorithmRepo.delete(foundAlgorithm.get())
      file.delete()
      it.success()
    }
  }

  @GetMapping("/{name}")
  fun download(request: HttpServletRequest, response: HttpServletResponse, @PathVariable name: String, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty) {
        return@create it.error(UserNotFoundException())
      }
      val file = File("moeaData/${principal.name}/algorithms/$name.class")
      if (!file.exists())
        return@create it.error(AlgorithmNotFoundOnServerException())
      response.contentType = "application/octet-stream"
      response.addHeader("Content-Disposition", "attachment; filename=$name.class")
      Files.copy(file.toPath(), response.outputStream)
      response.outputStream.flush()
      it.success()
    }
  }

}