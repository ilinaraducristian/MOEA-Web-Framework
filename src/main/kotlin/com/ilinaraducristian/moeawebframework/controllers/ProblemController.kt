package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.exceptions.ProblemExistsOnServerException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.UserNotFoundException
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import java.io.File
import java.security.Principal

@RestController
@RequestMapping("problem")
class ProblemController(
    private val userRepo: UserRepository
) {

  @PutMapping
  fun upload(@RequestParam("problem") problemFile: MultipartFile, @RequestParam("reference") referenceFile: MultipartFile, @RequestParam("override") override: Boolean = false, principal: Principal): Mono<Unit> {
    return mono {
      val existingFile = File("moeaData/${principal.name}/problems/${problemFile.originalFilename}")
      val existingReferenceFile = File("moeaData/${principal.name}/problems/references/${referenceFile.originalFilename}")
      if (existingFile.exists() && !override && existingReferenceFile.exists() && !override)
        throw ProblemExistsOnServerException()
      val user = userRepo.findByUsername(principal.name) ?: throw UserNotFoundException()
      user.problems.add(problemFile.originalFilename.toString().replace(""".class""", ""))
      userRepo.save(user)
      withContext(Dispatchers.IO) {
        problemFile.transferTo(File("moeaData/${principal.name}/problems/${problemFile.originalFilename}"))
        referenceFile.transferTo(File("moeaData/${principal.name}/problems/references/${referenceFile.originalFilename}"))
      }
    }
  }

  @DeleteMapping("{name}")
  fun delete(@PathVariable name: String, principal: Principal): Mono<Unit> {
    return mono {
      val user = userRepo.findByUsername(principal.name) ?: throw UserNotFoundException()
      if (!user.problems.contains(name)) {
        throw ProblemNotFoundException()
      }
      val file = File("moeaData/${principal.name}/problems/$name.class")
      file.delete()
      user.problems.remove(name)
      userRepo.save(user)
      return@mono
    }
  }

}