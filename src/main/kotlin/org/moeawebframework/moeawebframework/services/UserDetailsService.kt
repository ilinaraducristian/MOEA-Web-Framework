package org.moeawebframework.moeawebframework.services

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.*

class UserDetailsService(
//    private val
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
    println("""userRequest.accessToken: ${userRequest.accessToken}""")
    return MyOAuth2User()
  }

  private class MyOAuth2User : OAuth2User {
    override fun getAuthorities(): MutableCollection<GrantedAuthority> {
      return mutableListOf(SimpleGrantedAuthority("user"))
    }

    override fun getName(): String {
      return "user"
    }

    override fun getAttributes(): MutableMap<String, Any> {
      return mutableMapOf()
    }

  }

}