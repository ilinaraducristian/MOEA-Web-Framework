package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.entities.Algorithm
import com.ilinaraducristian.moeawebframework.entities.Problem
import com.ilinaraducristian.moeawebframework.exceptions.*
import com.ilinaraducristian.moeawebframework.repositories.AlgorithmRepository
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
@RequestMapping("public")
class PublicController(
    private val userRepo: UserRepository,
    private val problemRepo: ProblemRepository,
    private val algorithmRepo: AlgorithmRepository
) {

  @GetMapping("getProblems")
  fun getProblems(): Mono<Array<Problem>> {
    return Mono.create<Array<Problem>> {
      val foundUser = userRepo.findByUsername("admin")
      if(foundUser.isEmpty) {
        return@create it.error(InternalErrorException())
      }
      val admin = foundUser.get()
      it.success(problemRepo.findByUser(admin).toTypedArray())
    }
  }

  @GetMapping("getAlgorithms")
  fun getAlgorithms(): Mono<Array<Algorithm>> {
    return Mono.create<Array<Algorithm>> {
      val foundUser = userRepo.findByUsername("admin")
      if(foundUser.isEmpty) {
        return@create it.error(InternalErrorException())
      }
      val admin = foundUser.get()
      it.success(algorithmRepo.findByUser(admin).toTypedArray())
    }
  }

  @PutMapping("uploadProblem")
  fun uploadProblem(@RequestParam("file") file: MultipartFile, @RequestParam("override") override: Boolean): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername("admin")
      if (foundUser.isPresent) {
        val user = foundUser.get()
        val problem = Problem()
        problem.filePath = "moeaData/public/problems/${file.originalFilename}.class"
        problem.name = file.originalFilename.toString()
        user.problems.add(problem)
        problem.users.add(user)
        userRepo.save(user)
        val existingFile = File("moeaData/public/problems/${file.originalFilename}.class")
        if (existingFile.exists() && !override)
          return@create it.error(ProblemExistsOnServerException())
        file.transferTo(File("moeaData/public/problems/${file.originalFilename}.class"))
        it.success()
      } else {
        it.error(UserNotFoundException())
      }
    }
  }

  @DeleteMapping("deleteProblem/{name}")
  fun deleteProblem(@PathVariable name: String): Mono<Void> {
    return Mono.create<Void> {
      val file = File("moeaData/public/problems/$name.class")
      if (!file.exists()) {
        return@create it.error(ProblemNotFoundOnServerException())
      }
      val problem = problemRepo.findByName(name)
      if(problem.isEmpty) {
        return@create it.error(ProblemNotFoundException())
      }
      problemRepo.delete(problem.get())
      file.delete()
      it.success()
    }
  }

  @GetMapping("downloadProblem/{name}")
  fun downloadProblem(request: HttpServletRequest, response: HttpServletResponse, @PathVariable name: String): Mono<Void> {
    return Mono.create<Void> {
        val file = File("moeaData/public/problems/$name.class")
        if (!file.exists())
          return@create it.error(ProblemNotFoundOnServerException())
        response.contentType = "application/octet-stream"
        response.addHeader("Content-Disposition", "attachment; filename=$name.class")
        Files.copy(file.toPath(), response.outputStream)
        response.outputStream.flush()
        it.success()
    }
  }


  @PutMapping("uploadAlgorithm")
  fun uploadAlgorithm(@RequestParam("file") file: MultipartFile, @RequestParam("override") override: Boolean): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername("admin")
      if (foundUser.isPresent) {
        val user = foundUser.get()
        val algorithm = Algorithm()
        algorithm.filePath = "moeaData/public/algorithms/${file.originalFilename}.class"
        algorithm.name = file.originalFilename.toString()
        user.algorithms.add(algorithm)
        algorithm.users.add(user)
        userRepo.save(user)
        val existingFile = File("moeaData/public/algorithms/${file.originalFilename}.class")
        if (existingFile.exists() && !override)
          return@create it.error(AlgorithmExistsOnServerException())
        file.transferTo(File("moeaData/public/algorithms/${file.originalFilename}.class"))
        it.success()
      } else {
        it.error(UserNotFoundException())
      }
    }
  }

  @DeleteMapping("deleteAlgorithm/{name}")
  fun deleteAlgorithm(@PathVariable name: String): Mono<Void> {
    return Mono.create<Void> {
      val file = File("moeaData/public/algorithms/$name.class")
      if (!file.exists()) {
        return@create it.error(AlgorithmNotFoundOnServerException())
      }
      val algorithm = algorithmRepo.findByName(name)
      if(algorithm.isEmpty) {
        return@create it.error(AlgorithmNotFoundException())
      }
      algorithmRepo.delete(algorithm.get())
      file.delete()
      it.success()
    }
  }

  @GetMapping("downloadAlgorithm/{name}")
  fun downloadAlgorithm(request: HttpServletRequest, response: HttpServletResponse, @PathVariable name: String): Mono<Void> {
    return Mono.create<Void> {
      val file = File("moeaData/public/algorithms/$name.class")
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