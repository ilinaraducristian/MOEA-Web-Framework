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
import org.springframework.messaging.rsocket.retrieveAndAwait
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

//  suspend fun getUserData(id: String): Map<String, Array<out Any>?> {
//    val algorithms = algorithmDAO.getByUserEntityId(id)
//    val problems = problemDAO.getByUserEntityId(id)
//    val referenceSets = referenceSetDAO.getByUserEntityId(id)
//  println(algorithms?.size)
//    println(problems?.size)
//    println(referenceSets?.size)
//    return mapOf(Pair("algorithms", algorithms), Pair("problems", problems), Pair("referenceSets", referenceSets))
//  }

  suspend fun addQueueItem(id: String, queueItemDTO: QueueItemDTO): String {
    if (algorithmDAO.getByUserEntityIdAndMD5(id, queueItemDTO.algorithmMD5) == null) throw RuntimeException(AlgorithmNotFoundOrAccessDeniedException)
    if (problemDAO.getByUserEntityIdAndMD5(id, queueItemDTO.problemMD5) == null) throw RuntimeException(ProblemNotFoundOrAccessDeniedException)
    if (referenceSetDAO.getByUserEntityIdAndMD5(id, queueItemDTO.referenceSetMD5) == null) throw RuntimeException(ReferenceSetNotFoundOrAccessDeniedException)
    val uuid = UUID.randomUUID().toString()
    val queueItem = QueueItem(queueItemDTO, uuid)
    queueItemDAO.save(queueItem)
    return uuid
  }

  suspend fun getQueueItem(id: String, rabbitId: String): QueueItem? {
    return queueItemDAO.getByRabbitIdAndUserId(rabbitId, id)
  }

  suspend fun deleteQueueItem(id: String, rabbitId: String) {
    val queueItem = queueItemDAO.getByRabbitIdAndUserId(rabbitId, id)
        ?: throw RuntimeException(QueueItemNotFoundException)
    if (queueItem.status == "working") {
      cancelQueueItemProcessing(rabbitId)
    }
    queueItemDAO.delete(queueItem)
  }

  private suspend fun cancelQueueItemProcessing(rabbitId: String) {
    rSocketRequester.route("cancel")
        .data(rabbitId)
        .retrieveAndAwait<Unit>()
  }

}
