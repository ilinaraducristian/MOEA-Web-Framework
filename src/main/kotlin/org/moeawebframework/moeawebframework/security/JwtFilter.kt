package org.moeawebframework.moeawebframework.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

class JwtFilter(private val authenticationManager: ReactiveAuthenticationManager) : AuthenticationWebFilter(authenticationManager) {

}