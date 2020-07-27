package org.moeawebframework.moeawebframework.services

import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.entities.UserDao
import org.moeawebframework.moeawebframework.exceptions.BadCredentialsException
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserService(
    private val userDao: UserDao,
    private val encoder: BCryptPasswordEncoder
) {

  fun signup(signupInfoDTO: SignupInfoDTO): Mono<Void> {
    val user = User()
    user.username = signupInfoDTO.username
    user.password = encoder.encode(signupInfoDTO.password)
    user.email = signupInfoDTO.email
    user.firstName = signupInfoDTO.firstName
    user.lastName = signupInfoDTO.lastName
    return userDao.save(user).flatMap { Mono.empty<Void>() }
  }

  fun login(userCredentials: UserCredentialsDTO): Mono<RegisteredUserDTO> {
    return userDao.findByUsername(userCredentials.username)
        .switchIfEmpty {
          Mono.error(RuntimeException(UserNotFoundException))
        }.filter {
          encoder.matches(userCredentials.password, it.password)
        }.switchIfEmpty {
          Mono.error<User>(RuntimeException(BadCredentialsException))
        }.map {
          val registeredUserDTO = RegisteredUserDTO()
          registeredUserDTO.username = it.username
          registeredUserDTO.email = it.email
          registeredUserDTO.firstName = it.firstName
          registeredUserDTO.lastName = it.lastName
          registeredUserDTO
        }
  }

}