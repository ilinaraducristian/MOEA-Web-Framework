package com.ilinaraducristian.moeawebframework.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.dto.QualityIndicators
import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.moea.QueueItemSolver
import com.ilinaraducristian.moeawebframework.repositories.QueueItemRepository
import org.moeaframework.util.progress.ProgressListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class QueueItemSolverService(
    private val threadPoolTaskExecutor: ThreadPoolTaskExecutor,
    private val rabbitTemplate: RabbitTemplate,
    private val jsonConverter: ObjectMapper,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, QueueItem>,
    private val queueItemRepo: QueueItemRepository
) {

  val solvers = HashMap<String, QueueItemSolver>()

  fun solveQueueItem(queueItem: QueueItem) {

      val routingKey: String

      if (queueItem.user.username == "guest") {
        routingKey = "guest.${queueItem.rabbitId}"
      } else {
        routingKey = "user.${queueItem.user.username}.${queueItem.rabbitId}"
      }

      val progressListener = ProgressListener { event ->
        if (!event.isSeedFinished)
          return@ProgressListener
        try {
          val qualityIndicators = QualityIndicators(event.executor.instrumenter.lastAccumulator)
          qualityIndicators.currentSeed = event.currentSeed - 1
          queueItem.results.add(qualityIndicators)
          rabbitTemplate.convertAndSend(routingKey, jsonConverter.writeValueAsString(qualityIndicators))
        } catch (e: IllegalArgumentException) {
          // executor was canceled
        }
      }

      val queueItemSolver = QueueItemSolver(queueItem, progressListener)
    threadPoolTaskExecutor.submit {
      solvers[queueItem.rabbitId] = queueItemSolver
      var solved = false

      try {
        solved = queueItemSolver.solve()
        if (solved) {
          queueItem.status = "done"
          updateQueueItem(queueItem)
          rabbitTemplate.convertAndSend(routingKey, """{"status":"done"}""")
        }
      } catch (e: Exception) {
        println(e.printStackTrace())
        rabbitTemplate.convertAndSend(routingKey, """{"error":"${e.message}"}""")
      }
      if (!solved) {
        queueItem.results = ArrayList()
        queueItem.status = "waiting"
      }
      updateQueueItem(queueItem)

    }
  }

  fun cancelQueueItem(rabbitId: String): Boolean {
    val found = solvers[rabbitId]
    if (found != null) {
      found.cancel()
      solvers.remove(rabbitId)
      return true
    }
    return false
  }

  private fun updateQueueItem(queueItem: QueueItem) {
    if (queueItem.user.username == "guest") {
      reactiveRedisTemplate.opsForValue().set(queueItem.rabbitId, queueItem).block()
    } else {
      queueItemRepo.save(queueItem)
    }
  }

}