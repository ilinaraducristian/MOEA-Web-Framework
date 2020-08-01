package org.moeawebframework.moeawebframework.services

import org.moeawebframework.moeawebframework.dao.*
import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.entities.*
import org.moeawebframework.moeawebframework.exceptions.BadCredentialsException
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.io.File
import java.security.MessageDigest
import java.util.*

@Service
class UserService(
    private val userDao: UserDao,
    private val problemSolverDAO: ProblemSolverDAO,
    private val problemDAO: ProblemDAO,
    private val algorithmDAO: AlgorithmDAO,
    private val problemUserDAO: ProblemUserDAO,
    private val algorithmUserDAO: AlgorithmUserDAO,
    private val encoder: BCryptPasswordEncoder,
    private val hasher: MessageDigest
) {

  fun signup(signupInfoDTO: SignupInfoDTO): Mono<Void> {
    val moeawebframeworkUser = userDao.getByUsername("moeawebframework")
    problemUserDAO.getByUserUsername("moeawebframework").collectList().flatMap {
      
    }
//    moeawebframeworkUser.flatMap {
//      it.
//    }
    val user = User()
    user.username = signupInfoDTO.username
    user.password = encoder.encode(signupInfoDTO.password)
    user.email = signupInfoDTO.email
    user.firstName = signupInfoDTO.firstName
    user.lastName = signupInfoDTO.lastName
    return userDao.save(user).flatMap { Mono.empty<Void>() }
  }

  fun login(userCredentials: UserCredentialsDTO): Mono<RegisteredUserDTO> {
    return userDao.getByUsername(userCredentials.username)
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

  fun addProblemSolver(user: User, problemSolver: ProblemSolver): Mono<ProblemSolver> {
    problemSolver.userId = user.id
    return problemSolverDAO.save(problemSolver)
  }

  /**
   * @param user Must contain the real id from database
   * */
  fun uploadProblem(user: User, name: String, file: File): Mono<Void> {
    val hash = hasher.digest(file.readBytes())
    val b64Hash = Base64.getEncoder().encodeToString(hash)
    return problemDAO.findBySha256(b64Hash)
        .switchIfEmpty {
          // TODO("move problem from tmp folder to permanent storage")
          // TODO("if it's a microservice then check permanent storage as it must be common to all services")
          val newProblem = Problem()
          newProblem.name = name
          newProblem.sha256 = b64Hash
          problemDAO.save(newProblem)
        }
        .flatMap {
          problemUserDAO.getByUserId(user.id!!).next()
              .flatMap {
                Mono.error<ProblemUser>(RuntimeException("Problem exists"))
              }
              .switchIfEmpty {
                problemUserDAO.save(ProblemUser(null, user.id!!, it.id!!))
              }
        }
        .flatMap { Mono.empty<Void>() }
  }

  /**
   * The user must contain the real id from database.
   * */
  fun uploadAlgorithm(user: User, name: String, file: File): Mono<Void> {
    val hash = hasher.digest(file.readBytes())
    val b64Hash = Base64.getEncoder().encodeToString(hash)
    return algorithmDAO.findBySha256(b64Hash)
        .switchIfEmpty {
          // TODO("move algorithmUser from tmp folder to permanent storage")
          // TODO("if it's a microservice then check permanent storage as it must be common to all services")
          val newAlgorithm = Algorithm()
          newAlgorithm.name = name
          newAlgorithm.sha256 = b64Hash
          algorithmDAO.save(newAlgorithm)
        }
        .flatMap {
          algorithmUserDAO.getByUserId(user.id!!)
              .flatMap {
                Mono.error<AlgorithmUser>(RuntimeException("Algorithm exists"))
              }
              .switchIfEmpty {
                algorithmUserDAO.save(AlgorithmUser(null, user.id!!, it.id!!))
              }
        }
        .flatMap { Mono.empty<Void>() }
  }

}
