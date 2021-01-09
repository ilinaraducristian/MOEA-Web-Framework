package org.moeawebframework.moeawebframework.services

import org.moeawebframework.moeawebframework.RedisAdapter
import org.moeawebframework.moeawebframework.configs.default_algorithms
import org.moeawebframework.moeawebframework.configs.default_problems
import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.moeawebframework.moeawebframework.entities.QueueItem
import org.moeawebframework.moeawebframework.exceptions.QueueItemNotFoundException
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwaitOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class PublicService(
    private val redisAdapter: RedisAdapter,
    private val rSocketRequester: RSocketRequester
) {

  suspend fun getDefaultData(): Map<String, List<String>> {
    return mapOf(Pair("algorithms", default_algorithms), Pair("problems", default_problems))
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
    redisAdapter.set(newQueueItem.rabbitId, newQueueItem)
    return newQueueItem.rabbitId
  }

  suspend fun getQueueItem(rabbitId: String): QueueItem? {
    return redisAdapter.get(rabbitId)
  }

  suspend fun deleteQueueItem(rabbitId: String) {
    redisAdapter.get(rabbitId)
        ?: throw RuntimeException(QueueItemNotFoundException)
    cancelQueueItemProcessing(rabbitId)
    redisAdapter.delete(rabbitId)
  }

  suspend fun startProcessing(rabbitId: String) {
    val queueItem = redisAdapter.get(rabbitId)
        ?: throw RuntimeException(QueueItemNotFoundException)
    rSocketRequester.route("startProcessing")
        .data(queueItem)
        .retrieveAndAwaitOrNull<Unit>()
  }

  suspend fun cancelProcessing(rabbitId: String) {
    redisAdapter.get(rabbitId)
        ?: throw RuntimeException(QueueItemNotFoundException)
    cancelQueueItemProcessing(rabbitId)
  }

  private suspend fun cancelQueueItemProcessing(rabbitId: String) {
    rSocketRequester.route("cancelProcessing")
        .data(rabbitId)
        .retrieveAndAwaitOrNull<Unit>()
  }

}