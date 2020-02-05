package com.ilinaraducristian.moeawebframework.exceptions

class ExceptionResponse(exception: Exception) {
  val message = exception.message
}