package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.exceptions.ProblemExistsException
import com.ilinaraducristian.moeawebframework.exceptions.ProblemNotFoundException
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
class ProblemController {

  @PutMapping("upload")
  fun upload(@RequestParam("file") file: MultipartFile, @RequestParam("override") override: Boolean, principal: Principal?): Mono<Void> {
    return Mono.create<Void> {
      val existingFile = File("${principal?.name}/problems/${file.originalFilename}.class")
      if (existingFile.exists() && !override)
        it.error(ProblemExistsException())
      file.transferTo(File("${principal?.name}/problems/${file.originalFilename}"))
      it.success()
    }.doOnError { error ->
      throw error
    }
  }

  @GetMapping("download/{name}")
  fun download(request: HttpServletRequest, response: HttpServletResponse, @PathVariable name: String, principal: Principal?): Mono<Void> {
    return Mono.create<Void> {
      val file = File("${principal?.name}/problems/$name.class")
      if (!file.exists())
        it.error(ProblemNotFoundException())
      response.contentType = "application/octet-stream"
      response.addHeader("Content-Disposition", "attachment; filename=$name.class")
      Files.copy(file.toPath(), response.outputStream)
      response.outputStream.flush()
      it.success()
    }.doOnError { error ->
      throw error
    }

  }

}