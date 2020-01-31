package com.ilinaraducristian.moeawebframework.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RestAuthenticationEntryPoint : BasicAuthenticationEntryPoint() {

  override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
    response.addHeader("WWW-Authenticate", "Basic realm=$realmName")
    response.status = HttpServletResponse.SC_UNAUTHORIZED
    response.writer.println("HTTP Status 401 - " + authException.message)
  }

  override fun afterPropertiesSet() {
    realmName = "Realm?"
    super.afterPropertiesSet()
  }

}