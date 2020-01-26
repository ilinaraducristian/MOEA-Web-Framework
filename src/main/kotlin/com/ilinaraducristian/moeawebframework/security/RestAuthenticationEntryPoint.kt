package com.ilinaraducristian.moeawebframework.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RestAuthenticationEntryPoint: AuthenticationEntryPoint {

  override fun commence(request: HttpServletRequest?, response: HttpServletResponse?, authException: AuthenticationException?) {
    if(response == null) return
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
  }

}