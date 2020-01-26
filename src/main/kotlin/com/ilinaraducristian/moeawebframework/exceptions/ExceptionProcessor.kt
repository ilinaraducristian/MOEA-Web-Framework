package com.ilinaraducristian.moeawebframework.exceptions

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.RuntimeException

fun jsonHeaders(): HttpHeaders {
  val jsonHeaders = HttpHeaders()
  jsonHeaders.contentType = MediaType.APPLICATION_JSON
  return jsonHeaders
}

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class ExceptionProcessor : ResponseEntityExceptionHandler() {

  override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
    return ResponseEntity("JSON Parse Error: MainControllerExceptionProcessor", HttpStatus.CONFLICT)
  }

  @ExceptionHandler(value = [ProblemNotFoundException::class, AlgorithmNotFoundException::class])
  @ResponseStatus(HttpStatus.NOT_FOUND)
  fun handleFileNotFoundException(exception: RuntimeException): ExceptionResponse {
    return ExceptionResponse(exception)
  }

  @ExceptionHandler(value = [ProblemExistsException::class, AlgorithmExistsException::class])
  @ResponseStatus(HttpStatus.CONFLICT)
  fun handleFileExistsException(exception: RuntimeException): ExceptionResponse {
    return ExceptionResponse(exception)
  }

  @ExceptionHandler(value = [ProblemSolvedException::class, ProblemIsSolvingException::class])
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handleProblemSolvedException(exception: RuntimeException): ExceptionResponse {
    return ExceptionResponse(exception)
  }

  @ExceptionHandler(CannotCreateUserException::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun handleCannotCreateUserException(exception: RuntimeException): ExceptionResponse {
    return ExceptionResponse(exception)
  }

  @ExceptionHandler(UserNotFoundException::class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  fun handleUserNotFoundException(exception: RuntimeException): ExceptionResponse {
    return ExceptionResponse(exception)
  }

}