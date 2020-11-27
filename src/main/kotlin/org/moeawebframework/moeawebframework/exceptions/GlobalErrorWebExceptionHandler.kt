package org.moeawebframework.moeawebframework.exceptions

import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
@Order(-2)
class GlobalErrorWebExceptionHandler(
    errorAttributes: ErrorAttributes,
    applicationContext: ApplicationContext,
    serverCodecConfigurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(
    errorAttributes,
    ResourceProperties(),
    applicationContext
) {

  init {
    super.setMessageWriters(serverCodecConfigurer.writers)
    super.setMessageReaders(serverCodecConfigurer.readers)
  }

  override fun getRoutingFunction(errorAttributes: ErrorAttributes?): RouterFunction<ServerResponse> {
    return RouterFunctions.route(RequestPredicates.all(), HandlerFunction { request ->
//      val errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults())
      return@HandlerFunction ServerResponse.ok().bodyValue("On error response message")
    })
  }

}