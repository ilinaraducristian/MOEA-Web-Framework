package com.ilinaraducristian.moeawebframework.security

import com.ilinaraducristian.moeawebframework.JwtUtil
import io.jsonwebtoken.MalformedJwtException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtRequestFilter(
    private val userDetailsService: SecurityUserDetailsService,
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

  fun jwtFilter(request: HttpServletRequest) {

    if (SecurityContextHolder.getContext().authentication != null) {
      return
    }

    val authorizationHeader: String? = request.getHeader("Authorization")

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      return
    }
    val jwt = authorizationHeader.replace("Bearer ", "")
    val username: String

    try {
      username = jwtUtil.extractUsername(jwt)
    } catch (e: MalformedJwtException) {
      return
    }

    val userDetails = userDetailsService.loadUserByUsername(username) ?: return

    if (!jwtUtil.validateToken(jwt, userDetails)) {
      return
    }
    val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.authorities)
    usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
    SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
  }

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    jwtFilter(request)
    filterChain.doFilter(request, response)
  }

}