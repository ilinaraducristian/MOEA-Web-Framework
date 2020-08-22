package org.moeawebframework.moeawebframework.services

import org.moeawebframework.moeawebframework.dao.*
import org.moeawebframework.moeawebframework.dto.ProcessDTO
import org.moeawebframework.moeawebframework.dto.RegisteredUserDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.entities.*
import org.moeawebframework.moeawebframework.exceptions.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.http.codec.multipart.FilePart
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.security.MessageDigest
import java.util.*

@Service
class UserService(
    private val userDAO: UserDAO,
    private val processDAO: ProcessDAO,

    private val algorithmDAO: AlgorithmDAO,
    private val problemDAO: ProblemDAO,
    private val referenceSetDAO: ReferenceSetDAO,

    private val algorithmUserDAO: AlgorithmUserDAO,
    private val problemUserDAO: ProblemUserDAO,
    private val referenceSetUserDAO: ReferenceSetUserDAO,

    private val encoder: BCryptPasswordEncoder,
    private val hasher: MessageDigest,
    private val rSocketRequester: RSocketRequester
) {

  @Value("CDN_URI")
  lateinit var CDN_URI: String

  fun signup(user: User): Mono<User> {
    return userDAO.getByUsername(user.username)
        .flatMap {
          Mono.error<User>(RuntimeException(UserExistsException))
        }
        .switchIfEmpty {
          userDAO.save(user)
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
    return userDAO.getByUsername(userCredentials.username)
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

  fun oauth2Login(username: String): Mono<RegisteredUserDTO> {
    return userDAO.getByUsername(username)
        .switchIfEmpty {
          Mono.error(RuntimeException(UserNotFoundException))
        }.map {
          val registeredUserDTO = RegisteredUserDTO()
          registeredUserDTO.username = it.username
          registeredUserDTO.email = it.email
          registeredUserDTO.firstName = it.firstName
          registeredUserDTO.lastName = it.lastName
          registeredUserDTO
        }
  }

  fun uploadAlgorithm(userId: Long, name: String, filePart: FilePart): Mono<String> {
    var b64Hash = ""
    return filePart.content().last().flatMap {
      val bytes = ByteArray(it.readableByteCount())
      it.read(bytes)
      DataBufferUtils.release(it)
      val hash = hasher.digest(bytes)
      b64Hash = Base64.getEncoder().encodeToString(hash)
      val multipart = MultipartBodyBuilder()
      multipart.part("data", filePart).filename(b64Hash)
      algorithmDAO.getBySha256(b64Hash).switchIfEmpty {
        WebClient.create(CDN_URI).post()
            .uri("/")
            .body(BodyInserters.fromMultipartData(multipart.build()))
            .exchange().flatMap {
              val newAlgorithm = Algorithm()
              newAlgorithm.name = name
              newAlgorithm.sha256 = b64Hash
              algorithmDAO.save(newAlgorithm)
            }
      }
    }.flatMap {
      algorithmUserDAO.getByUserIdAndAlgorithmId(userId, it.id!!)
          .flatMap {
            Mono.error<AlgorithmUser>(RuntimeException(AlgorithmExistsException))
          }
          .switchIfEmpty {
            algorithmUserDAO.save(AlgorithmUser(null, userId, it.id!!))
          }
    }.map { b64Hash }
  }

  fun uploadProblem(userId: Long, name: String, filePart: FilePart): Mono<String> {
    var b64Hash = ""
    return filePart.content().last().flatMap {
      val bytes = ByteArray(it.readableByteCount())
      it.read(bytes)
      DataBufferUtils.release(it)
      val hash = hasher.digest(bytes)
      b64Hash = Base64.getEncoder().encodeToString(hash)
      val multipart = MultipartBodyBuilder()
      multipart.part("data", filePart).filename(b64Hash)
      problemDAO.getBySha256(b64Hash).switchIfEmpty {
        WebClient.create(CDN_URI).post()
            .uri("/")
            .body(BodyInserters.fromMultipartData(multipart.build()))
            .exchange().flatMap {
              val newProblem = Problem()
              newProblem.name = name
              newProblem.sha256 = b64Hash
              problemDAO.save(newProblem)
            }
      }
    }.flatMap {
      problemUserDAO.getByUserIdAndProblemId(userId, it.id!!)
          .flatMap {
            Mono.error<ProblemUser>(RuntimeException(ProblemExistsException))
          }
          .switchIfEmpty {
            problemUserDAO.save(ProblemUser(null, userId, it.id!!))
          }
    }.map { b64Hash }
  }

  fun uploadReferenceSet(userId: Long, name: String, filePart: FilePart): Mono<String> {
    var b64Hash = ""
    return filePart.content().last().flatMap {
      val bytes = ByteArray(it.readableByteCount())
      it.read(bytes)
      DataBufferUtils.release(it)
      val hash = hasher.digest(bytes)
      b64Hash = Base64.getEncoder().encodeToString(hash)
      val multipart = MultipartBodyBuilder()
      multipart.part("data", filePart).filename(b64Hash)
      referenceSetDAO.getBySha256(b64Hash).switchIfEmpty {
        WebClient.create(CDN_URI).post()
            .uri("/")
            .body(BodyInserters.fromMultipartData(multipart.build()))
            .exchange().flatMap {
              val newReferenceSet = ReferenceSet()
              newReferenceSet.name = name
              newReferenceSet.sha256 = b64Hash
              referenceSetDAO.save(newReferenceSet)
            }
      }
    }.flatMap {
      referenceSetUserDAO.getByUserIdAndReferenceSetId(userId, it.id!!)
          .flatMap {
            Mono.error<ReferenceSetUser>(RuntimeException(ReferenceSetExistsException))
          }
          .switchIfEmpty {
            referenceSetUserDAO.save(ReferenceSetUser(null, userId, it.id!!))
          }
    }.map { b64Hash }
  }

  fun addProcess(username: String, processDTO: ProcessDTO): Mono<String> {
    return userDAO.getByUsername(username)
        .switchIfEmpty(Mono.error(RuntimeException(UserNotFoundException)))
        .flatMap { user ->
          if (!problemDAO.existsBySha256(processDTO.problemSha256)) return@flatMap Mono.error<String>(RuntimeException(ProblemNotFoundException))
          if (!algorithmDAO.existsBySha256(processDTO.algorithmSha256)) return@flatMap Mono.error<String>(RuntimeException(AlgorithmNotFoundException))
          if (!referenceSetDAO.existsBySha256(processDTO.referenceSetSha256)) return@flatMap Mono.error<String>(RuntimeException(ReferenceSetNotFoundException))
          val newProcess = Process()
          newProcess.name = processDTO.name
          newProcess.userId = user.id!!
          newProcess.numberOfEvaluations = processDTO.numberOfEvaluations
          newProcess.numberOfSeeds = processDTO.numberOfSeeds
          newProcess.problemSha256 = processDTO.problemSha256
          newProcess.algorithmSha256 = processDTO.algorithmSha256
          newProcess.referenceSetSha256 = processDTO.referenceSetSha256
          newProcess.rabbitId = UUID.randomUUID().toString()
          processDAO.save(newProcess).map { """{"rabbitId": "${newProcess.rabbitId}"}""" }
        }
  }

  fun process(rabbitId: String): Mono<Unit> {
    return processDAO.getByRabbitId(rabbitId)
        .switchIfEmpty(Mono.error(RuntimeException(ProcessNotFoundException)))
        .flatMap {
          if (it.status == "processing" || it.status == "processed")
            return@flatMap Mono.error<Unit>(RuntimeException(it.status))

          rSocketRequester.route("process")
              .data(it)
              .retrieveMono(Unit::class.java)
        }
  }

}
