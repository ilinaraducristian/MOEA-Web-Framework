package com.ilinaraducristian.moeawebframework.controllers

import com.ilinaraducristian.moeawebframework.entities.QueueItem
import com.ilinaraducristian.moeawebframework.moea.QueueItemSolver
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("test")
class TestController (
    private val redisTemplate: ReactiveRedisTemplate<String, QueueItem>
){

  @GetMapping()
  fun testRoute(){
//    val qi = QueueItem()

//    redisTemplate.opsForValue().get("asd").map {
//      println("map:")
//      println(it)
//      "asd"
//    }.doOnSuccess {
//      println("Success")
//      println(it)
//    }.subscribe()

  }

}