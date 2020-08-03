package org.moeawebframework.moeawebframework.services

import org.moeawebframework.moeawebframework.dao.*
import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.entities.*
import org.moeawebframework.moeawebframework.exceptions.BadCredentialsException
import org.moeawebframework.moeawebframework.exceptions.UserExistsException
import org.moeawebframework.moeawebframework.exceptions.UserNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
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

  fun signup(user: User): Mono<User> {
    return userDao.getByUsername(user.username)
        .flatMap {
          Mono.error<User>(RuntimeException(UserExistsException))
        }
        .switchIfEmpty {
          userDao.save(user)
        }
        .flatMapMany {
          user.id = it.id
          problemUserDAO.getByUserUsername("moeawebframework")
        }
        .flatMap {
          val newProblemUser = ProblemUser()
          newProblemUser.userId = user.id!!
          newProblemUser.problemId = it.problemId
          problemUserDAO.save(newProblemUser)
        }
        .collectList()
        .map { user }
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
  fun uploadProblem(userId: Long, name: String, file: File): Mono<ProblemUser> {
    val hash = hasher.digest(file.readBytes())
    val b64Hash = Base64.getEncoder().encodeToString(hash)
    return problemDAO.getBySha256(b64Hash)
        .switchIfEmpty {
          // TODO("move problem from tmp folder to permanent storage")
          // TODO("if it's a microservice then check permanent storage as it must be common to all services")
          val newProblem = Problem()
          newProblem.name = name
          newProblem.sha256 = b64Hash
          problemDAO.save(newProblem)
        }
        .flatMap {
          problemUserDAO.getByUserIdAndProblemId(userId, it.id!!)
              .flatMap {
                Mono.error<ProblemUser>(RuntimeException("Problem exists"))
              }
              .switchIfEmpty {
                problemUserDAO.save(ProblemUser(null, userId, it.id!!))
              }
        }
  }

  /**
   * The user must contain the real id from database.
   * */
  fun uploadAlgorithm(userId: Long, name: String, file: File): Mono<Void> {
    val hash = hasher.digest(file.readBytes())
    val b64Hash = Base64.getEncoder().encodeToString(hash)
    return algorithmDAO.getBySha256(b64Hash)
        .switchIfEmpty {
          // TODO("move algorithmUser from tmp folder to permanent storage")
          // TODO("if it's a microservice then check permanent storage as it must be common to all services")
          val newAlgorithm = Algorithm()
          newAlgorithm.name = name
          newAlgorithm.sha256 = b64Hash
          algorithmDAO.save(newAlgorithm)
        }
        .flatMap {
          algorithmUserDAO.getByUserIdAndAlgorithmId(userId, it.id!!)
              .flatMap {
                Mono.error<AlgorithmUser>(RuntimeException("Algorithm exists"))
              }
              .switchIfEmpty {
                algorithmUserDAO.save(AlgorithmUser(null, userId, it.id!!))
              }
        }
        .flatMap { Mono.empty<Void>() }
  }

}
