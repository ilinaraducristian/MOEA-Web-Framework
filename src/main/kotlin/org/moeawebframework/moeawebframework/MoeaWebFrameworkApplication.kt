package org.moeawebframework.moeawebframework

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity

@SpringBootApplication
@EnableWebFluxSecurity
class MoeaWebFrameworkApplication

fun main(args: Array<String>) {
  runApplication<MoeaWebFrameworkApplication>(*args)
}
