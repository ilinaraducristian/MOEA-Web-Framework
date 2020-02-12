package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.repositories.ProblemRepository
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
@RequestMapping("problem")
class ProblemController(
    private val userRepo: UserRepository,
    private val problemRepo: ProblemRepository
) {

  @PutMapping("upload")
  fun upload(@RequestParam("file") file: MultipartFile, @RequestParam("override") override: Boolean, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isPresent) {
        val user = foundUser.get()
        val problem = Problem()
        problem.filePath = "moeaData/users/${principal.name}/problems/${file.originalFilename}.class"
        problem.name = file.originalFilename.toString()
        user.problems.add(problem)
        problem.users.add(user)
        userRepo.save(user)
        val existingFile = File("${principal.name}/problems/${file.originalFilename}.class")
        if (existingFile.exists() && !override)
          return@create it.error(ProblemExistsOnServerException())
        file.transferTo(File("${principal.name}/problems/${file.originalFilename}"))
        it.success()
      } else {
        it.error(UserNotFoundException())
      }
    }
  }

  @DeleteMapping("delete/{name}")
  fun delete(@PathVariable name: String, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (!foundUser.isPresent) {
        return@create it.error(UserNotFoundException())
      }
      val user = foundUser.get()
      val foundProblem = problemRepo.findByUserAndName(user, name)
      if(foundProblem.isEmpty) {
        return@create it.error(ProblemNotFoundException())
      }
      val file = File("moeaData/users/${principal.name}/problems/$name.class")
      if (!file.exists()) {
        return@create it.error(ProblemNotFoundOnServerException())
      }
      problemRepo.delete(foundProblem.get())
      file.delete()
      it.success()
    }
  }

  @GetMapping("download/{name}")
  fun download(request: HttpServletRequest, response: HttpServletResponse, @PathVariable name: String, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isPresent) {
        val file = File("moeaData/users/${principal.name}/problems/$name.class")
        if (!file.exists())
          return@create it.error(ProblemNotFoundOnServerException())
        response.contentType = "application/octet-stream"
        response.addHeader("Content-Disposition", "attachment; filename=$name.class")
        Files.copy(file.toPath(), response.outputStream)
        response.outputStream.flush()
        it.success()
      } else {
        it.error(UserNotFoundException())
      }
    }
  }

}