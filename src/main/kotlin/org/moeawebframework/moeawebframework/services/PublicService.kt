package org.moeawebframework.moeawebframework.services

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.configs.redisType
import org.moeawebframework.moeawebframework.dao.UserDAO
import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.moeawebframework.moeawebframework.entities.QueueItem
import org.moeawebframework.moeawebframework.exceptions.AlreadyProcessedException
import org.moeawebframework.moeawebframework.exceptions.AlreadyProcessingException
import org.moeawebframework.moeawebframework.exceptions.ProcessNotFoundException
import org.moeawebframework.moeawebframework.exceptions.QueueItemNotFoundException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwaitOrNull
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class PublicService(
    private val userDAO: UserDAO,
    private val redisTemplate: ReactiveRedisTemplate<String, redisType>,
    private val rSocketRequester: RSocketRequester
) {

  suspend fun getDefaultData(): Map<String, List<Any>> {
    val algorithms = listOf("CMA-ES", "NSGAII", "NSGAIII", "GDE3", "eMOEA", "eNSGAII", "MOEAD", "MSOPS", "SPEA2", "PAES", "PESA2", "OMOPSO", "SMPSO", "IBEA", "SMS-EMOA", "VEGA", "DBEA", "RVEA", "RSO")
    val problems = listOf("Belegundu", "DTLZ1_2", "DTLZ2_2", "DTLZ3_2", "DTLZ4_2", "DTLZ7_2", "ROT_DTLZ1_2", "ROT_DTLZ2_2", "ROT_DTLZ3_2", "ROT_DTLZ4_2", "ROT_DTLZ7_2", "UF1", "UF2", "UF3", "UF4", "UF5", "UF6", "UF7", "UF8", "UF9", "UF10", "UF11", "UF12", "UF13", "CF1", "CF2", "CF3", "CF4", "CF5", "CF6", "CF7", "CF8", "CF9", "CF10", "LZ1", "LZ2", "LZ3", "LZ4", "LZ5", "LZ6", "LZ7", "LZ8", "LZ9", "WFG1_2", "WFG2_2", "WFG3_2", "WFG4_2", "WFG5_2", "WFG6_2", "WFG7_2", "WFG8_2", "WFG9_2", "ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT5", "ZDT6", "Binh", "Binh2", "Binh3", "Binh4", "Fonseca", "Fonseca2", "Jimenez", "Kita", "Kursawe", "Laumanns", "Lis", "Murata", "Obayashi", "OKA1", "OKA2", "Osyczka", "Osyczka2", "Poloni", "Quagliarella", "Rendon", "Rendon2", "Schaffer", "Schaffer2", "Srinivas", "Tamaki", "Tanaka", "Viennet", "Viennet2", "Viennet3", "Viennet4")
    return mapOf(Pair("algorithms", algorithms), Pair("problems", problems), Pair("referenceSets", problems))
  }

  suspend fun addQueueItem(queueItemDTO: QueueItemDTO): String {
    val newProcess = QueueItem()
    newProcess.name = queueItemDTO.name
    newProcess.numberOfEvaluations = queueItemDTO.numberOfEvaluations
    newProcess.numberOfSeeds = queueItemDTO.numberOfSeeds
    newProcess.problemMD5 = queueItemDTO.problemMD5
    newProcess.algorithmMD5 = queueItemDTO.algorithmMD5
    newProcess.referenceSetMD5 = queueItemDTO.referenceSetMD5
    newProcess.rabbitId = UUID.randomUUID().toString()
    return redisTemplate.opsForValue().set(newProcess.rabbitId, newProcess)
        .map { newProcess.rabbitId }.awaitSingle()
  }

  suspend fun getQueueItem(rabbitId: String): QueueItem? {
    return redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull()
  }

  suspend fun deleteQueueItem(rabbitId: String) {
    redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull()
        ?: throw RuntimeException(QueueItemNotFoundException)
    cancelQueueItemProcessing(rabbitId)
    redisTemplate.opsForValue().delete(rabbitId).awaitFirstOrNull()
  }

  suspend fun startProcessing(rabbitId: String) {
    redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull()
        ?: throw RuntimeException(QueueItemNotFoundException)
    rSocketRequester.route("startProcessing")
        .data(rabbitId)
        .retrieveAndAwaitOrNull<Unit>()
  }

  suspend fun cancelProcessing(rabbitId: String) {
    redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull()
        ?: throw RuntimeException(QueueItemNotFoundException)
    cancelQueueItemProcessing(rabbitId)
  }

  private suspend fun cancelQueueItemProcessing(rabbitId: String) {
    rSocketRequester.route("cancelProcessing")
        .data(rabbitId)
        .retrieveAndAwaitOrNull<Unit>()
  }

}