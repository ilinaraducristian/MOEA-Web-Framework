package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.entities.User
import com.ilinaraducristian.moeawebframework.moea.QueueItemSolver
import com.ilinaraducristian.moeawebframework.services.QueueItemSolverService
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("test")
class TestController(
    private val queueItemSolverService: QueueItemSolverService
) {

  @GetMapping
  fun testRoute() {
    val queueItem = QueueItem()
    queueItem.name = "Asd"
    queueItem.problem = "Belegundu"
    queueItem.algorithm = "AcoR"
    queueItem.numberOfSeeds = 10
    queueItem.numberOfEvaluations = 10000
    queueItem.user = User(username = "user")
    queueItem.rabbitId = "plsasdl"
    queueItemSolverService.solveQueueItem(queueItem)
  }

}