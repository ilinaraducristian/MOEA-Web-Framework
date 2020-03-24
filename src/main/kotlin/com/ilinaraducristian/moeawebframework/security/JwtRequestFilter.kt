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

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    val authorizationHeader: String? = request.getHeader("Authorization")

    var username: String? = null
    var jwt: String? = null

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.replace("Bearer ", "")
      try {
        username = jwtUtil.extractUsername(jwt)
      }catch(e: MalformedJwtException){
      }
    }

    if (username != null && SecurityContextHolder.getContext().authentication == null) {
      val userDetails = userDetailsService.loadUserByUsername(username)
      if (jwt != null && userDetails != null) {
        if (jwtUtil.validateToken(jwt, userDetails)) {
          val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.authorities)
          usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
          SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
        }
      }
    }
    filterChain.doFilter(request, response)
  }

}