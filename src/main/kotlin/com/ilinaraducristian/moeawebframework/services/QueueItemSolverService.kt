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

  val solvers = HashMap<UUID, QueueItemSolver>()

  fun solveQueueItem(queueItem: QueueItem, isUser: Boolean = true): String {
    var solverId: UUID
    do {
      solverId = UUID.randomUUID()
    } while (solvers.contains(solverId))
    val progressListener = ProgressListener { event ->
      if (!event.isSeedFinished)
        return@ProgressListener
      try {
        val qualityIndicators = QualityIndicators(event.executor.instrumenter.lastAccumulator, event.currentSeed - 1)
        queueItem.results.add(qualityIndicators)
        if (isUser) {
          rabbitTemplate.convertAndSend("user.${queueItem.user.username}.${queueItem.rabbitId}", jsonConverter.writeValueAsString(qualityIndicators))
        } else {
          rabbitTemplate.convertAndSend("guest.${queueItem.rabbitId}", jsonConverter.writeValueAsString(qualityIndicators))
        }
      } catch (e: IllegalArgumentException) {
        // executor was canceled
      }
    }
    val queueItemSolver = QueueItemSolver(queueItem, progressListener)
    solvers[solverId] = queueItemSolver
    var solved = false

    threadPoolTaskExecutor.submit {
      try {
        solved = queueItemSolver.solve()
        if (solved) {
          queueItem.status = "done"
          if (isUser) {
            queueItemRepo.save(queueItem)
            rabbitTemplate.convertAndSend("user.${queueItem.user.username}.${queueItem.rabbitId}", """{"status":"done"}""")
          } else {
            reactiveRedisTemplate.opsForValue().set(queueItem.rabbitId, queueItem).block()
            rabbitTemplate.convertAndSend("guest.${queueItem.rabbitId}", """{"status":"done"}""")
          }
        }
      } catch (e: Exception) {
        if (isUser) {
          rabbitTemplate.convertAndSend("user.${queueItem.user.username}.${queueItem.rabbitId}", """{"error":"${e.message}"}""")
        } else {
          rabbitTemplate.convertAndSend("guest.${queueItem.rabbitId}", """{"error":"${e.message}"}""")
        }
      } finally {
        if (!solved) {
          queueItem.results = ArrayList()
          queueItem.status = "waiting"
        }
        if (isUser) {
          queueItem.solverId = null
          queueItemRepo.save(queueItem)
        } else {
          reactiveRedisTemplate.opsForValue().set(queueItem.rabbitId, queueItem)
        }
      }
    }
    return solverId.toString()
  }

  fun cancelQueueItem(solverId: UUID) {
    val found = solvers[solverId]
    if (found != null) {
      found.cancel()
      solvers.remove(solverId)
    }
  }

}