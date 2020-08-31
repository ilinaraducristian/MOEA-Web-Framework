package org.moeawebframework.moeawebframework.services

import org.moeawebframework.moeawebframework.configs.redisType
import org.moeawebframework.moeawebframework.dao.AlgorithmDAO
import org.moeawebframework.moeawebframework.dao.ProblemDAO
import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.dto.ProcessDTO
import org.moeawebframework.moeawebframework.entities.Process
import org.moeawebframework.moeawebframework.exceptions.*
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class GuestService(
    private val userDAO: UserDAO,
    private val problemDAO: ProblemDAO,
    private val algorithmDAO: AlgorithmDAO,
    private val redisTemplate: ReactiveRedisTemplate<String, redisType>,
    private val rSocketRequester: RSocketRequester
) {

  fun addProcess(processDTO: ProcessDTO): Mono<String> {
    if (!problemDAO.existsBySha256(processDTO.problemSha256)) return Mono.error<String>(RuntimeException(ProblemNotFoundException))
    if (!algorithmDAO.existsBySha256(processDTO.algorithmSha256)) return Mono.error<String>(RuntimeException(AlgorithmNotFoundException))
    val newProcess = Process()
    newProcess.name = processDTO.name
    newProcess.numberOfEvaluations = processDTO.numberOfEvaluations
    newProcess.numberOfSeeds = processDTO.numberOfSeeds
    newProcess.problemSha256 = processDTO.problemSha256
    newProcess.algorithmSha256 = processDTO.algorithmSha256
    newProcess.referenceSetSha256 = processDTO.referenceSetSha256
    newProcess.rabbitId = UUID.randomUUID().toString()
    return redisTemplate.opsForValue().set(newProcess.rabbitId, newProcess).map { """{"rabbitId": "${newProcess.rabbitId}"}""" }
  }

  fun process(rabbitId: String): Mono<Unit> {
    return redisTemplate.opsForValue()
        .get(rabbitId)
        .switchIfEmpty(Mono.error(RuntimeException(ProcessNotFoundException)))
        .flatMap {
          if(it.status == "processing")
            return@flatMap Mono.error<Unit>(RuntimeException(AlreadyProcessingException))
          if (it.status == "processed")
            return@flatMap Mono.error<Unit>(RuntimeException(AlreadyProcessedException))

          rSocketRequester.route("process")
              .data(it)
              .retrieveMono(Unit::class.java)
        }
  }

}