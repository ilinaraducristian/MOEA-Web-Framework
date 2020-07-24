package org.moeawebframework.moeawebframework

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
class MoeaWebFrameworkApplication

fun main(args: Array<String>) {
  runApplication<MoeaWebFrameworkApplication>(*args)
}
