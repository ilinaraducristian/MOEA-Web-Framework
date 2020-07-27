package org.moeawebframework.moeawebframework.services

import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.entities.User
import org.moeawebframework.moeawebframework.entities.UserDao
import org.moeawebframework.moeawebframework.exceptions.BadCredentialsException
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserService(
    private val userDao: UserDao
) {

  suspend fun signup(signupInfoDTO: SignupInfoDTO) = mono{
    val user = User()
    user.username = signupInfoDTO.username
    user.password = signupInfoDTO.password
    user.email = signupInfoDTO.email
    user.firstName = signupInfoDTO.firstName
    user.lastName = signupInfoDTO.lastName
    userDao.save(user)
    return@mono
  }

  suspend fun login(userCredentials: UserCredentialsDTO) = mono{
    val user = userDao.findByUsername(userCredentials.username)
    println("LOGIN")
    println(user)
    return@mono
//        if(user == null)
//        .switchIfEmpty {
//      Mono.error<User>(RuntimeException(UserNotFoundException))
//    }.flatMap {
//      if (it.password == userCredentials.password) {
//        val registeredUserDTO = RegisteredUserDTO()
//        registeredUserDTO.username = it.username
//        registeredUserDTO.email = it.email
//        registeredUserDTO.firstName = it.firstName
//        registeredUserDTO.lastName = it.lastName
//        return@flatMap Mono.just(registeredUserDTO)
//      } else {
//        return@flatMap Mono.error<RegisteredUserDTO>(RuntimeException(BadCredentialsException))
//      }
//    }
  }

}