package org.moeawebframework.moeawebframework.services

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.moeawebframework.moeawebframework.configs.default_algorithms
import org.moeawebframework.moeawebframework.configs.default_problems
import org.moeawebframework.moeawebframework.configs.redisType
import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.moeawebframework.moeawebframework.entities.QueueItem
import org.moeawebframework.moeawebframework.exceptions.QueueItemNotFoundException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwaitOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class PublicService(
    private val redisTemplate: ReactiveRedisTemplate<String, redisType>,
    private val rSocketRequester: RSocketRequester
) {

  suspend fun getDefaultData(): Map<String, List<Any>> {
    val algorithms = default_algorithms.map { mapOf(Pair("name", it), Pair("md5", it)) }
    val problems = default_problems.map { mapOf(Pair("name", it), Pair("md5", it)) }
    return mapOf(Pair("algorithms", algorithms), Pair("problems", problems))
  }

  suspend fun addQueueItem(queueItemDTO: QueueItemDTO): String {
    val newQueueItem = QueueItem()
    newQueueItem.name = queueItemDTO.name
    newQueueItem.numberOfEvaluations = queueItemDTO.numberOfEvaluations
    newQueueItem.numberOfSeeds = queueItemDTO.numberOfSeeds
    newQueueItem.problemMD5 = queueItemDTO.problemMD5
    newQueueItem.algorithmMD5 = queueItemDTO.algorithmMD5
    newQueueItem.referenceSetMD5 = queueItemDTO.referenceSetMD5
    newQueueItem.rabbitId = UUID.randomUUID().toString()
    return redisTemplate.opsForValue().set(newQueueItem.rabbitId, newQueueItem)
        .map { newQueueItem.rabbitId }.awaitSingle()
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
    val queueItem = redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull()
        ?: throw RuntimeException(QueueItemNotFoundException)
    rSocketRequester.route("startProcessing")
        .data(queueItem)
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