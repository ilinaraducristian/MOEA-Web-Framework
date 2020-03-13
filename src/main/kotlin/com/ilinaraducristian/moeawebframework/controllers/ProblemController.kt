package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.exceptions.ProblemExistsOnServerException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundOnServerException
import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
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

    @PutMapping
    fun upload(@RequestParam("problem") problemFile: MultipartFile, @RequestParam("reference") referenceFile: MultipartFile, @RequestParam("override") override: Boolean = false, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val existingFile = File("moeaData/${principal.name}/problems/${problemFile.originalFilename}")
      val existingReferenceFile = File("moeaData/${principal.name}/problems/references/${referenceFile.originalFilename}")
      if (existingFile.exists() && !override && existingReferenceFile.exists() && !override)
        return@create it.error(ProblemExistsOnServerException())
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty) {
        it.error(UserNotFoundException())
      }
      val user = foundUser.get()
      val problem = Problem()
      problem.name = problemFile.originalFilename.toString().replace(""".class""", "")
      user.problems.add(problem)
      problem.users.add(user)
      userRepo.save(user)
      problemFile.transferTo(File("moeaData/${principal.name}/problems/${problemFile.originalFilename}"))
      referenceFile.transferTo(File("moeaData/${principal.name}/problems/references/${referenceFile.originalFilename}"))
      it.success()
    }
  }

  @DeleteMapping("{name}")
  fun delete(@PathVariable name: String, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (!foundUser.isPresent) {
        return@create it.error(UserNotFoundException())
      }
      val user = foundUser.get()
      val foundProblem = problemRepo.findByUsersAndName(user, name)
      if (foundProblem.isEmpty) {
        return@create it.error(ProblemNotFoundException())
      }
      val file = File("moeaData/${principal.name}/problems/$name.class")
      if (!file.exists()) {
        return@create it.error(ProblemNotFoundOnServerException())
      }
      problemRepo.delete(foundProblem.get())
      file.delete()
      it.success()
    }
  }

  @GetMapping("{name}")
  fun download(request: HttpServletRequest, response: HttpServletResponse, @PathVariable name: String, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername(principal.name)
      if (foundUser.isEmpty) return@create it.error(UserNotFoundException())
      val file = File("moeaData/${principal.name}/problems/$name.class")
      if (!file.exists())
        return@create it.error(ProblemNotFoundOnServerException())
      response.contentType = "application/octet-stream"
      response.addHeader("Content-Disposition", "attachment; filename=$name.class")
      Files.copy(file.toPath(), response.outputStream)
      response.outputStream.flush()
      it.success()
    }
  }

}