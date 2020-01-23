package com.ilinaraducristian.moeawebframework.exceptions

import java.lang.RuntimeException

class ExceptionResponse(exception: RuntimeException) {
  val message = exception.message
}