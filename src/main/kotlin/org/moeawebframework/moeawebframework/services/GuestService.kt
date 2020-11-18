package org.moeawebframework.moeawebframework.services

import org.moeawebframework.moeawebframework.configs.redisType
import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.moeawebframework.moeawebframework.entities.QueueItem
import org.moeawebframework.moeawebframework.exceptions.AlreadyProcessedException
import org.moeawebframework.moeawebframework.exceptions.AlreadyProcessingException
import org.moeawebframework.moeawebframework.exceptions.ProcessNotFoundException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class GuestService(
    private val userDAO: UserDAO,
    private val redisTemplate: ReactiveRedisTemplate<String, redisType>,
    private val rSocketRequester: RSocketRequester
) {

  fun addProcess(queueItemDTO: QueueItemDTO): Mono<String> {
    val newProcess = QueueItem()
    newProcess.name = queueItemDTO.name
    newProcess.numberOfEvaluations = queueItemDTO.numberOfEvaluations
    newProcess.numberOfSeeds = queueItemDTO.numberOfSeeds
    newProcess.problemMD5 = queueItemDTO.problemMD5
    newProcess.algorithmMD5 = queueItemDTO.algorithmMD5
    newProcess.referenceSetMD5 = queueItemDTO.referenceSetMD5
    newProcess.rabbitId = UUID.randomUUID().toString()
    return redisTemplate.opsForValue().set(newProcess.rabbitId, newProcess).map { """{"rabbitId": "${newProcess.rabbitId}"}""" }
  }

  fun process(rabbitId: String): Mono<Unit> {
    return redisTemplate.opsForValue()
        .get(rabbitId)
        .switchIfEmpty(Mono.error(RuntimeException(ProcessNotFoundException)))
        .flatMap {
          if (it.status == "processing")
            return@flatMap Mono.error<Unit>(RuntimeException(AlreadyProcessingException))
          if (it.status == "processed")
            return@flatMap Mono.error<Unit>(RuntimeException(AlreadyProcessedException))

          rSocketRequester.route("process")
              .data(it)
              .retrieveMono(Unit::class.java)
        }
  }

}