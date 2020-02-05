package com.ilinaraducristian.moeawebframework.exceptions

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

private data class Violation(val fieldName: String, val message: String?)

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class ExceptionProcessor : ResponseEntityExceptionHandler() {

  override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
    return ResponseEntity("Invalid JSON", HttpStatus.BAD_REQUEST)
  }

  override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
    val violations = ArrayList<Violation>()
    for (fieldError in ex.bindingResult.fieldErrors) {
      violations.add(Violation(fieldError.field, fieldError.defaultMessage))
    }
    return ResponseEntity(violations, HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(value = [ProblemNotFoundException::class, AlgorithmNotFoundException::class, UserNotFoundException::class])
  @ResponseStatus(HttpStatus.NOT_FOUND)
  fun handleNotFoundExceptions(exception: RuntimeException): ExceptionResponse {
    return ExceptionResponse(exception)
  }

  @ExceptionHandler(value = [ProblemExistsOnServerException::class, ProblemExistsException::class, AlgorithmExistsOnServerException::class, AlgorithmExistsException::class])
  @ResponseStatus(HttpStatus.CONFLICT)
  fun handleConflictExceptions(exception: RuntimeException): ExceptionResponse {
    return ExceptionResponse(exception)
  }

  @ExceptionHandler(value = [ProblemSolvedException::class, ProblemIsSolvingException::class, ProblemIsNotSolvingException::class])
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handleBadRequestExceptions(exception: RuntimeException): ExceptionResponse {
    return ExceptionResponse(exception)
  }

  @ExceptionHandler(CannotCreateUserException::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun handleInternalServerExceptions(exception: RuntimeException): ExceptionResponse {
    return ExceptionResponse(exception)
  }

  @ExceptionHandler(BadCredentialsException::class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  fun handleUnauthorizedExceptions(exception: RuntimeException): ExceptionResponse {
    return ExceptionResponse(exception)
  }

}