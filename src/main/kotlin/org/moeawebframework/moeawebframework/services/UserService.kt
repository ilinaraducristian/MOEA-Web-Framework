package org.moeawebframework.moeawebframework.services

import org.moeawebframework.moeawebframework.dao.*
import org.moeawebframework.moeawebframework.dto.*
import org.moeawebframework.moeawebframework.entities.*
import org.moeawebframework.moeawebframework.exceptions.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.http.codec.multipart.FilePart
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.security.MessageDigest
import java.util.*
import kotlin.collections.HashMap

@Service
class UserService(
    private val userDAO: UserDAO,
    private val processDAO: ProcessDAO,

    private val algorithmDAO: AlgorithmDAO,
    private val problemDAO: ProblemDAO,

    private val algorithmUserDAO: AlgorithmUserDAO,
    private val problemUserDAO: ProblemUserDAO,

    private val hasher: MessageDigest,
    private val rSocketRequester: RSocketRequester
) {

  @Value("\${cdn_url}")
  lateinit var cdn_url: String

  @Value("\${keycloak_signup_url}")
  lateinit var keycloak_signup_url: String

  @Value("\${keycloak_login_url}")
  lateinit var keycloak_login_url: String

  fun signup(signupInfoDTO: SignupInfoDTO): Mono<User> {
    // TODO what happens if userDAO.save(user) when user exists
    return userDAO.getByUsername(signupInfoDTO.username)
        .flatMap {
          Mono.error<User>(RuntimeException(UserExistsException))
        }
        .switchIfEmpty {
          val multipartBodyBuilder = MultipartBodyBuilder()
          multipartBodyBuilder.part("client_id", "moeawebframework")
          multipartBodyBuilder.part("grant_type", "password")
          multipartBodyBuilder.part("username", signupInfoDTO.username)
          multipartBodyBuilder.part("password", signupInfoDTO.password)
          multipartBodyBuilder.part("first_name", signupInfoDTO.firstName)
          if(signupInfoDTO.lastName != null)
            multipartBodyBuilder.part("last_name", signupInfoDTO.lastName!!)
          multipartBodyBuilder.part("email", signupInfoDTO.email)
          return@switchIfEmpty WebClient.create(keycloak_signup_url).post()
              .uri("/")
              .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
              .exchange()
              .flatMap {
                userDAO.save(User(username = signupInfoDTO.username))
              }
        }
  }

  fun login(userCredentialsDTO: UserCredentialsDTO): Mono<AccessTokenDTO> {
    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("username", userCredentialsDTO.username)
    multipartBodyBuilder.part("password", userCredentialsDTO.password)
    return WebClient.create(keycloak_login_url).post()
        .uri("/")
        .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
        .exchange().flatMap {clientResponse ->
          clientResponse.bodyToMono(AccessTokenDTO::class.java)
        }
  }

  fun getAlgorithmsAndProblems(userId: Long): Mono<HashMap<String, List<Any>>> {
    return Mono.zip(
        algorithmUserDAO.getByUserId(userId)
            .flatMap { algorithmDAO.get(it.algorithmId) }
            .collectList(),
        problemUserDAO.getByUserId(userId)
            .flatMap { problemDAO.get(it.problemId) }
            .collectList()
    )
        .map {
          val algorithmsAndProblems = HashMap<String, List<Any>>()
          algorithmsAndProblems["algorithms"] = it.t1
          algorithmsAndProblems["problems"] = it.t2
          algorithmsAndProblems
        }
  }

  fun oauth2Login(username: String): Mono<RegisteredUserDTO> {
    return userDAO.getByUsername(username)
        .switchIfEmpty {
          Mono.error(RuntimeException(UserNotFoundException))
        }.map {
          val registeredUserDTO = RegisteredUserDTO()
          registeredUserDTO.username = it.username
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
        WebClient.create(cdn_url).post()
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

  fun uploadProblem(userId: Long, name: String, problemFilePart: FilePart, referenceSetFilePart: FilePart): Mono<String> {
    var problemB64Hash = ""
    var referenceSetB64Hash = ""
    var problemBytes: ByteArray = byteArrayOf()
    var referenceSetBytes: ByteArray = byteArrayOf()
    return Mono.zip(
        problemFilePart.content().last().map {
          val bytes = ByteArray(it.readableByteCount())
          it.read(bytes)
          DataBufferUtils.release(it)
          problemBytes = bytes
        },
        referenceSetFilePart.content().last().map {
          val bytes = ByteArray(it.readableByteCount())
          it.read(bytes)
          DataBufferUtils.release(it)
          referenceSetBytes = bytes
        }
    )
        .then(problemDAO.getBySha256(problemB64Hash))
        .switchIfEmpty {
          problemB64Hash = Base64.getEncoder().encodeToString(hasher.digest(problemBytes))
          referenceSetB64Hash = Base64.getEncoder().encodeToString(hasher.digest(referenceSetBytes))
          val problemMultipart = MultipartBodyBuilder()
          val referenceSetMultipart = MultipartBodyBuilder()
          problemMultipart.part("data", problemFilePart).filename(problemB64Hash)
          referenceSetMultipart.part("data", referenceSetFilePart).filename(referenceSetB64Hash)
          return@switchIfEmpty Mono.zip(
              WebClient.create(cdn_url).post()
                  .uri("/")
                  .body(BodyInserters.fromMultipartData(problemMultipart.build()))
                  .exchange(),
              WebClient.create(cdn_url).post()
                  .uri("/")
                  .body(BodyInserters.fromMultipartData(referenceSetMultipart.build()))
                  .exchange()
          ).flatMap {
            val newProblem = Problem()
            newProblem.name = name
            newProblem.problemSha256 = problemB64Hash
            newProblem.referenceSetSha256 = referenceSetB64Hash
            problemDAO.save(newProblem)
          }
        }
        .flatMap {
          problemUserDAO.getByUserIdAndProblemId(userId, it.id!!)
              .flatMap {
                Mono.error<ProblemUser>(RuntimeException(ProblemExistsException))
              }
              .switchIfEmpty {
                problemUserDAO.save(ProblemUser(null, userId, it.id!!))
              }
        }
        .map { """{"problemB64Hash": "$problemB64Hash", "referenceSetB64Hash": "$referenceSetB64Hash"}""" }
  }

  fun addProcess(username: String, processDTO: ProcessDTO): Mono<String> {
    return userDAO.getByUsername(username)
        .switchIfEmpty(Mono.error(RuntimeException(UserNotFoundException)))
        .flatMap { user ->
          if (!algorithmDAO.existsBySha256(processDTO.algorithmSha256)) return@flatMap Mono.error<String>(RuntimeException(AlgorithmNotFoundException))
          if (!problemDAO.existsBySha256(processDTO.problemSha256)) return@flatMap Mono.error<String>(RuntimeException(ProblemNotFoundException))
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
