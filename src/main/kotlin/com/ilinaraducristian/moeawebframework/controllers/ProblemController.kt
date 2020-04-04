package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.exceptions.ProblemExistsOnServerException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundOnServerException
import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
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
    private val userRepo: UserRepository
) {

    @PutMapping
    fun upload(@RequestParam("problem") problemFile: MultipartFile, @RequestParam("reference") referenceFile: MultipartFile, @RequestParam("override") override: Boolean = false, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val existingFile = File("moeaData/${principal.name}/problems/${problemFile.originalFilename}")
      val existingReferenceFile = File("moeaData/${principal.name}/problems/references/${referenceFile.originalFilename}")
      if (existingFile.exists() && !override && existingReferenceFile.exists() && !override)
        return@create it.error(ProblemExistsOnServerException())
      val user = userRepo.findByUsername(principal.name) ?: return@create it.error(UserNotFoundException())
      user.problems.add(problemFile.originalFilename.toString().replace(""".class""", ""))
      userRepo.save(user)
      problemFile.transferTo(File("moeaData/${principal.name}/problems/${problemFile.originalFilename}"))
      referenceFile.transferTo(File("moeaData/${principal.name}/problems/references/${referenceFile.originalFilename}"))
      it.success()
    }
  }

  @DeleteMapping("{name}")
  fun delete(@PathVariable name: String, principal: Principal): Mono<Void> {
    return Mono.create<Void> {
      val user = userRepo.findByUsername(principal.name) ?: return@create it.error(UserNotFoundException())
      if (!user.problems.contains(name)) {
        return@create it.error(ProblemNotFoundException())
      }
      val file = File("moeaData/${principal.name}/problems/$name.class")
      file.delete()
      user.problems.remove(name)
      userRepo.save(user)
      it.success()
    }
  }

}