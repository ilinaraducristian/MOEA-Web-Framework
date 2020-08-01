package org.moeawebframework.moeawebframework.configs

import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier
import org.moeawebframework.moeawebframework.services.UserDetailsService
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import java.net.MalformedURLException
import java.security.MessageDigest


@Configuration
class MainConfig {

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  fun encoder(): BCryptPasswordEncoder {
    return BCryptPasswordEncoder()
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  fun hasher(): MessageDigest {
    return MessageDigest.getInstance("SHA-256")
  }

  @Bean
  fun userDetailsService(): OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    return UserDetailsService()
  }

}