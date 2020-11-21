package org.moeawebframework.moeawebframework.controllers

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import org.moeawebframework.moeawebframework.dto.QueueItemDTO
import org.moeawebframework.moeawebframework.entities.QueueItem
import org.moeawebframework.moeawebframework.exceptions.ProcessNotFoundException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.MediaType
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("queue")
class PublicQueueController(
    private val redisTemplate: ReactiveRedisTemplate<String, QueueItem>,
    private val rSocketRequester: RSocketRequester
) {

//  @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
//  suspend fun addProcess(@RequestBody queueItemDTO: QueueItemDTO): String {
//    var newUUID = UUID.randomUUID().toString()
//    var queueItem: QueueItem? = null
//    var count = 0
//
//    while (queueItem != null) {
//      if (++count == 100) break
//      newUUID = UUID.randomUUID().toString()
//      queueItem = redisTemplate.opsForValue().get(newUUID).awaitFirstOrNull()
//    }
//    if (queueItem == null) {
//      redisTemplate.opsForValue().set(newUUID, QueueItem(queueItemDTO, newUUID)).awaitLast()
//      return """{"rabbitId": "$newUUID"}"""
//    }
//    // error too many retries ( > 100 )
//    System.err.println("Too many retries, something went wrong")
//    throw RuntimeException("""{"error": "Internal error"}""")
//  }

  @PostMapping("process/{rabbitId}")
  suspend fun process(@PathVariable rabbitId: String) {
    val process = redisTemplate.opsForValue().get(rabbitId).awaitFirstOrNull()
        ?: throw RuntimeException(ProcessNotFoundException)
    if (process.status == "processing" || process.status == "processed")
      throw RuntimeException(process.status)
    try {
      rSocketRequester.route("process")
          .data(process)
          .retrieveMono(Unit::class.java)
          .awaitFirstOrNull()
    } catch (e: Exception) {
      e.printStackTrace()
      println("Exception in process, should never happen")
    }
  }

}