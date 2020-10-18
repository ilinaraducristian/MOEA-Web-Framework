package org.moeawebframework.moeawebframework.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.withContext
import org.moeawebframework.moeawebframework.dao.*
import org.moeawebframework.moeawebframework.dto.KeycloakTokenDTO
import org.moeawebframework.moeawebframework.dto.ProcessDTO
import org.moeawebframework.moeawebframework.dto.SignupInfoDTO
import org.moeawebframework.moeawebframework.dto.UserCredentialsDTO
import org.moeawebframework.moeawebframework.entities.*
import org.moeawebframework.moeawebframework.exceptions.*
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpStatus
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.http.codec.multipart.FilePart
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwait
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono
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
    private val rSocketRequester: RSocketRequester,
    private val httpService: HttpService
) {

  suspend fun signup(signupInfoDTO: SignupInfoDTO) {
    val user = userDAO.getByUsername(signupInfoDTO.username).awaitFirstOrNull()
    if (user != null) throw RuntimeException(UsernameTakenException)
    if (user?.email == signupInfoDTO.email) throw RuntimeException(EmailTakenException)
    httpService.keycloakSignup(signupInfoDTO)
    userDAO.save(User(username = signupInfoDTO.username)).awaitLast()
  }

  suspend fun login(userCredentialsDTO: UserCredentialsDTO): KeycloakTokenDTO {
    val clientResponse = httpService.keycloakLogin(userCredentialsDTO)
    if (clientResponse.statusCode() != HttpStatus.OK) throw RuntimeException(BadCredentialsException)
    return clientResponse.awaitBody()
  }

  suspend fun delete(id: String): ClientResponse {
    return httpService.keycloakDelete(id)
  }

  suspend fun getAlgorithmsAndProblems(userId: Long): HashMap<String, List<Any>> {
    val algorithmsAndProblems1 = Mono.zip(
        algorithmUserDAO.getByUserId(userId)
            .flatMap { algorithmDAO.get(it.algorithmId) }
            .collectList(),
        problemUserDAO.getByUserId(userId)
            .flatMap { problemDAO.get(it.problemId) }
            .collectList()
    ).awaitLast()
    val algorithmsAndProblems = HashMap<String, List<Any>>()
    algorithmsAndProblems["algorithms"] = algorithmsAndProblems1.t1
    algorithmsAndProblems["problems"] = algorithmsAndProblems1.t2
    return algorithmsAndProblems
  }

  suspend fun uploadAlgorithm(userId: Long, name: String, filePart: FilePart): String {
    var b64Hash = ""
    val dataBuffer = filePart.content().last().awaitLast()
    val bytes = ByteArray(dataBuffer.readableByteCount())
    dataBuffer.read(bytes)
    DataBufferUtils.release(dataBuffer)
    val hash = hasher.digest(bytes)
    b64Hash = Base64.getEncoder().encodeToString(hash)
    val multipart = MultipartBodyBuilder()
    multipart.part("data", filePart).filename(b64Hash)
    var algorithm = algorithmDAO.getBySha256(b64Hash).awaitFirstOrNull()
    if (algorithm == null) {
      httpService.uploadToCDN(BodyInserters.fromMultipartData(multipart.build()))
      val newAlgorithm = Algorithm()
      newAlgorithm.name = name
      newAlgorithm.sha256 = b64Hash
      algorithm = algorithmDAO.save(newAlgorithm).awaitLast()
    }
    val algorithm2 = algorithmUserDAO.getByUserIdAndAlgorithmId(userId, algorithm?.id!!).awaitFirstOrNull()
    if (algorithm2 != null) throw RuntimeException(AlgorithmExistsException)
    algorithmUserDAO.save(AlgorithmUser(null, userId, algorithm.id!!)).awaitLast()
    return b64Hash
  }

  suspend fun uploadProblem(userId: Long, name: String, problemFilePart: FilePart, referenceSetFilePart: FilePart): String {
    var problemB64Hash = ""
    var referenceSetB64Hash = ""
    var problemBytes: ByteArray = byteArrayOf()
    var referenceSetBytes: ByteArray = byteArrayOf()

    Mono.zip(
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
    ).awaitLast()
    var problem = problemDAO.getBySha256(problemB64Hash).awaitFirstOrNull()
    if (problem == null) {
      problemB64Hash = Base64.getEncoder().encodeToString(hasher.digest(problemBytes))
      referenceSetB64Hash = Base64.getEncoder().encodeToString(hasher.digest(referenceSetBytes))
      val problemMultipart = MultipartBodyBuilder()
      val referenceSetMultipart = MultipartBodyBuilder()
      problemMultipart.part("data", problemFilePart).filename(problemB64Hash)
      referenceSetMultipart.part("data", referenceSetFilePart).filename(referenceSetB64Hash)
      withContext(Dispatchers.Default) {
        httpService.uploadToCDN(BodyInserters.fromMultipartData(problemMultipart.build()))
        httpService.uploadToCDN(BodyInserters.fromMultipartData(referenceSetMultipart.build()))
      }
      val newProblem = Problem()
      newProblem.name = name
      newProblem.problemSha256 = problemB64Hash
      newProblem.referenceSetSha256 = referenceSetB64Hash
      problem = problemDAO.save(newProblem).awaitLast()
    }
    val problem2 = problemUserDAO.getByUserIdAndProblemId(userId, problem?.id!!).awaitFirstOrNull()
    if (problem2 != null) throw RuntimeException(ProblemExistsException)
    problemUserDAO.save(ProblemUser(null, userId, problem.id!!)).awaitLast()
    return """{"problemB64Hash": "$problemB64Hash", "referenceSetB64Hash": "$referenceSetB64Hash"}"""
  }

  suspend fun addProcess(username: String, processDTO: ProcessDTO): String {
    val user = userDAO.getByUsername(username).awaitFirstOrNull() ?: throw RuntimeException(UserNotFoundException)
    if (!algorithmDAO.existsBySha256(processDTO.algorithmSha256)) throw (RuntimeException(AlgorithmNotFoundException))
    if (!problemDAO.existsBySha256(processDTO.problemSha256)) throw (RuntimeException(ProblemNotFoundException))
    val newProcess = Process(processDTO, UUID.randomUUID().toString())
    newProcess.userId = user.id!!
    processDAO.save(newProcess).awaitLast()
    return """{"rabbitId": "${newProcess.rabbitId}"}"""
  }

  suspend fun process(rabbitId: String) {
    val process = processDAO.getByRabbitId(rabbitId).awaitFirstOrNull()
        ?: throw RuntimeException(ProcessNotFoundException)
    if (process.status == "processing" || process.status == "processed")
      throw RuntimeException(process.status)

    rSocketRequester.route("process")
        .data(process)
        .retrieveAndAwait<Unit>()
  }

}
