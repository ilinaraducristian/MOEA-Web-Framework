package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmExistsException
import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemExistsException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Files
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("algorithm")
class AlgorithmController {

  @PutMapping("upload")
  fun upload(@RequestParam("file") file: MultipartFile, @RequestParam("override") override: Boolean): Mono<Void> {
    return Mono.create<Void> {
      val existingFile = File("algorithms/${file.originalFilename}.class")
      if (existingFile.exists() && !override)
        it.error(AlgorithmExistsException())
      file.transferTo(File("algorithms/${file.originalFilename}"))
      it.success()
    }.doOnError { error ->
      throw error
    }
  }

  @GetMapping("download/{name}")
  fun download(request: HttpServletRequest, response: HttpServletResponse, @PathVariable name: String): Mono<Void> {
    return Mono.create<Void> {
      val file = File("algorithms/$name.class")
      if (!file.exists())
        it.error(AlgorithmNotFoundException())
      response.contentType = "application/octet-stream"
      response.addHeader("Content-Disposition", "attachment; filename=$name.class")
      Files.copy(file.toPath(), response.outputStream)
      response.outputStream.flush()
      it.success()
    }.doOnError{ error ->
      throw error
    }
  }

}