package com.ilinaraducristian.moeawebframework

import kotlinx.coroutines.reactor.mono
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Mono
import java.lang.RuntimeException

@SpringBootApplication
class MoeawebframeworkApplication

fun main(args: Array<String>) {
  runApplication<MoeawebframeworkApplication>(*args)
}
