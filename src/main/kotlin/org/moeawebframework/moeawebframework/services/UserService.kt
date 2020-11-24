package org.moeawebframework.moeawebframework.services

import org.moeawebframework.moeawebframework.dao.AlgorithmDAO
import org.moeawebframework.moeawebframework.dao.ProblemDAO
import org.moeawebframework.moeawebframework.dao.QueueItemDAO
import org.moeawebframework.moeawebframework.dao.ReferenceSetDAO
import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.moeawebframework.moeawebframework.entities.QueueItem
import org.moeawebframework.moeawebframework.exceptions.AlgorithmNotFoundOrAccessDeniedException
import org.moeawebframework.moeawebframework.exceptions.ProblemNotFoundOrAccessDeniedException
import org.moeawebframework.moeawebframework.exceptions.QueueItemNotFoundException
import org.moeawebframework.moeawebframework.exceptions.ReferenceSetNotFoundOrAccessDeniedException
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwaitOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val queueItemDAO: QueueItemDAO,

    private val algorithmDAO: AlgorithmDAO,
    private val problemDAO: ProblemDAO,
    private val referenceSetDAO: ReferenceSetDAO,

    private val rSocketRequester: RSocketRequester
) {

  suspend fun getUserData(userEntityId: String): Map<String, List<Any>> {
    val algorithms = algorithmDAO.getByUserEntityId(userEntityId).map { mapOf(Pair("name", it.name), Pair("md5", it.md5)) }
    val problems = problemDAO.getByUserEntityId(userEntityId).map { mapOf(Pair("name", it.name), Pair("md5", it.md5)) }
    val referenceSets = referenceSetDAO.getByUserEntityId(userEntityId).map { mapOf(Pair("name", it.name), Pair("md5", it.md5)) }
    val queueItems = queueItemDAO.getByUserEntityId(userEntityId)
    return mapOf(Pair("algorithms", algorithms), Pair("problems", problems), Pair("referenceSets", referenceSets), Pair("queueItems", queueItems))
  }

  suspend fun addQueueItem(userEntityId: String, queueItemDTO: QueueItemDTO): String {
    if (algorithmDAO.getByUserEntityIdAndMD5(userEntityId, queueItemDTO.algorithmMD5) == null) throw RuntimeException(AlgorithmNotFoundOrAccessDeniedException)
    if (problemDAO.getByUserEntityIdAndMD5(userEntityId, queueItemDTO.problemMD5) == null) throw RuntimeException(ProblemNotFoundOrAccessDeniedException)
    if (referenceSetDAO.getByUserEntityIdAndMD5(userEntityId, queueItemDTO.referenceSetMD5) == null) throw RuntimeException(ReferenceSetNotFoundOrAccessDeniedException)
    val uuid = UUID.randomUUID().toString()
    val queueItem = QueueItem(queueItemDTO, uuid, userEntityId)
    queueItemDAO.save(queueItem)
    return uuid
  }

  suspend fun getQueueItem(userEntityId: String, rabbitId: String): QueueItem? {
    return queueItemDAO.getByUserEntityIdAndRabbitId(userEntityId, rabbitId)
  }

  suspend fun deleteQueueItem(userEntityId: String, rabbitId: String) {
    val queueItem = queueItemDAO.getByUserEntityIdAndRabbitId(userEntityId, rabbitId)
        ?: throw RuntimeException(QueueItemNotFoundException)
    if (queueItem.status == "working") {
      cancelQueueItemProcessing(rabbitId)
    }
    queueItemDAO.delete(queueItem)
  }

  suspend fun startProcessing(userEntityId: String, rabbitId: String) {
    val queueItem = queueItemDAO.getByUserEntityIdAndRabbitId(userEntityId, rabbitId)
        ?: throw RuntimeException(QueueItemNotFoundException)
    rSocketRequester.route("startProcessing")
        .data(queueItem)
        .retrieveAndAwaitOrNull<Unit>()
  }

  suspend fun cancelProcessing(userEntityId: String, rabbitId: String) {
    queueItemDAO.getByUserEntityIdAndRabbitId(userEntityId, rabbitId)
        ?: throw RuntimeException(QueueItemNotFoundException)
    cancelQueueItemProcessing(rabbitId)
  }

  private suspend fun cancelQueueItemProcessing(rabbitId: String) {
    rSocketRequester.route("cancelProcessing")
        .data(rabbitId)
        .retrieveAndAwaitOrNull<Unit>()
  }

}
