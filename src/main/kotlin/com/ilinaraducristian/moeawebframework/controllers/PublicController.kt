package com.ilinaraducristian.moeawebframework.controllers

import com.fasterxml.jackson.databind.ObjectMapper
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
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("public")
@CrossOrigin
class PublicController(
    private val userRepo: UserRepository,
    private val problemRepo: ProblemRepository,
    private val algorithmRepo: AlgorithmRepository,
    private val jsonConverter: ObjectMapper
) {

  @GetMapping("getProblemsAndAlgorithms")
  fun getProblemsAndAlgorithms(): Mono<String> {
    return Mono.create<String> {
      val foundUser = userRepo.findByUsername("admin")
      if(foundUser.isEmpty) {
        return@create it.error(InternalErrorException())
      }
      val admin = foundUser.get()
      it.success("""{"problems": ${jsonConverter.writeValueAsString(problemRepo.findByUsers(admin).map {problem -> problem.name})}, "algorithms": ${jsonConverter.writeValueAsString(algorithmRepo.findByUsers(admin).map{algorithm -> algorithm.name})}}""")
    }
  }

  @GetMapping("getProblems")
  fun getProblems(): Mono<List<String>> {
    return Mono.create<List<String>> {
      val foundUser = userRepo.findByUsername("admin")
      if(foundUser.isEmpty) {
        return@create it.error(InternalErrorException())
      }
      val admin = foundUser.get()
      it.success(problemRepo.findByUsers(admin).map{problem -> problem.name})
    }
  }

  @GetMapping("getAlgorithms")
  fun getAlgorithms(): Mono<List<String>> {
    return Mono.create<List<String>> {
      val foundUser = userRepo.findByUsername("admin")
      if(foundUser.isEmpty) {
        return@create it.error(InternalErrorException())
      }
      val admin = foundUser.get()
      it.success(algorithmRepo.findByUsers(admin).map{algorithm -> algorithm.name})
    }
  }

  @PutMapping("uploadProblem")
  fun uploadProblem(@RequestParam("file") file: MultipartFile, @RequestParam("override") override: Boolean): Mono<Void> {
    return Mono.create<Void> {
      val foundUser = userRepo.findByUsername("admin")
      if (foundUser.isPresent) {
        val user = foundUser.get()
        val problem = Problem()
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