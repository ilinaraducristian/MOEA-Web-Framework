package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmExistsException
import com.ilinaraducristian.moeawebframework.exceptions.AlgorithmNotFoundException
import com.ilinaraducristian.moeawebframework.exceptions.UserNotLoggedInException
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
class AlgorithmController {

  @PutMapping("upload")
  fun upload(@RequestParam("file") file: MultipartFile, @RequestParam("override") override: Boolean, principal: Principal?): Mono<Void> {
    return Mono.create<Void> {
      if (principal == null) {
        return@create it.error(UserNotLoggedInException())
      }
      val existingFile = File("${principal.name}/algorithms/${file.originalFilename}.class")
      if (existingFile.exists() && !override)
        return@create it.error(AlgorithmExistsException())
      file.transferTo(File("${principal.name}/algorithms/${file.originalFilename}"))
      it.success()

    }
  }

  @GetMapping("download/{name}")
  fun download(request: HttpServletRequest, response: HttpServletResponse, @PathVariable name: String, principal: Principal?): Mono<Void> {
    return Mono.create<Void> {
      if (principal == null)
        return@create it.error(UserNotLoggedInException())
      val file = File("${principal.name}/algorithms/$name.class")
      if (!file.exists())
        return@create it.error(AlgorithmNotFoundException())
      response.contentType = "application/octet-stream"
      response.addHeader("Content-Disposition", "attachment; filename=$name.class")
      Files.copy(file.toPath(), response.outputStream)
      response.outputStream.flush()
      it.success()

    }
  }

}