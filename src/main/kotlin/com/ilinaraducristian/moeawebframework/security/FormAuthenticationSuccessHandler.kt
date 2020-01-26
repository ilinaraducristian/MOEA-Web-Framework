package com.ilinaraducristian.moeawebframework.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.security.web.savedrequest.SavedRequest
import org.springframework.util.StringUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FormAuthenticationSuccessHandler: SimpleUrlAuthenticationSuccessHandler() {
  private var requestCache: RequestCache = HttpSessionRequestCache()

  override fun onAuthenticationSuccess(
      request: HttpServletRequest,
      response: HttpServletResponse?,
      authentication: Authentication?) {
    val savedRequest: SavedRequest? = requestCache.getRequest(request, response)
    if (savedRequest == null) {
      clearAuthenticationAttributes(request)
      return
    }
    val targetUrlParam = targetUrlParameter
    if (isAlwaysUseDefaultTargetUrl
        || (targetUrlParam != null
            && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
      requestCache.removeRequest(request, response)
      clearAuthenticationAttributes(request)
      return
    }
    clearAuthenticationAttributes(request)
  }

  fun setRequestCache(requestCache: RequestCache) {
    this.requestCache = requestCache
  }
}