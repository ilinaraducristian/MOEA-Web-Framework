package com.ilinaraducristian.moeawebframework.exceptions

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
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

  @ExceptionHandler(value = [java.lang.RuntimeException::class])
  fun handleRuntimeExceptions(ex: RuntimeException): ResponseEntity<String> {
    val statusCode: Int = try {
      Integer.parseInt(ex.message!!.substring(1..3))
    } catch (e: NumberFormatException) {
      500
    }
    val errorMessage = ex.message!!.substring(6 until ex.message!!.length)
    val headers = HttpHeaders()
    headers.set("Content-Type", "application/json")
    return ResponseEntity(""" {"error": "$errorMessage"} """, headers, HttpStatus.valueOf(statusCode))
  }

}