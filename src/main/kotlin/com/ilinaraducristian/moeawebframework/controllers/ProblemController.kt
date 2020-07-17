package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.exceptions.ProblemExistsOnServerException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import com.ilinaraducristian.moeawebframework.security.UserPrincipal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import java.io.File

@RestController
@RequestMapping("problem")
class ProblemController(
    private val userRepo: UserRepository
) {

  @PutMapping
  fun upload(@RequestParam("problem") problemFile: MultipartFile, @RequestParam("reference") referenceFile: MultipartFile, @RequestParam("override") override: Boolean = false, authentication: Authentication): Mono<Unit> {
    return mono {
      val existingFile = File("moeaData/${authentication.name}/problems/${problemFile.originalFilename}")
      val existingReferenceFile = File("moeaData/${authentication.name}/problems/references/${referenceFile.originalFilename}")
      if (existingFile.exists() && !override && existingReferenceFile.exists() && !override)
        throw RuntimeException(ProblemExistsOnServerException)
      val user = (authentication.principal as UserPrincipal).user
      user.problems.add(problemFile.originalFilename.toString().replace(""".class""", ""))
      userRepo.save(user)
      withContext(Dispatchers.IO) {
        problemFile.transferTo(File("moeaData/${authentication.name}/problems/${problemFile.originalFilename}"))
        referenceFile.transferTo(File("moeaData/${authentication.name}/problems/references/${referenceFile.originalFilename}"))
      }
    }
  }

  @DeleteMapping("{name}")
  fun delete(@PathVariable name: String, authentication: Authentication): Mono<Unit> {
    return mono {
      val user = (authentication.principal as UserPrincipal).user
      if (!user.problems.contains(name)) {
        throw RuntimeException(ProblemNotFoundException)
      }
      val file = File("moeaData/${authentication.name}/problems/$name.class")
      file.delete()
      user.problems.remove(name)
      userRepo.save(user)
      return@mono
    }
  }

}