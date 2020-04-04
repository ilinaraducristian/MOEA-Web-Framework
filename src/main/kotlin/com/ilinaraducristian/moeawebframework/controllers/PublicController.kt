package com.ilinaraducristian.moeawebframework.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilinaraducristian.moeawebframework.repositories.UserRepository
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("public")
@CrossOrigin
class PublicController(
    private val userRepo: UserRepository,
    private val jsonConverter: ObjectMapper
) {

  @GetMapping
  fun isServerOnline() {}

  @GetMapping("getProblems")
  fun getProblems(): Mono<Array<String>> {
    return Mono.create<Array<String>> {
      it.success(problems)
    }
  }

  @GetMapping("getAlgorithms")
  fun getAlgorithms(): Mono<Array<String>> {
    return Mono.create<Array<String>> {
      it.success(algorithms)
    }
  }

}