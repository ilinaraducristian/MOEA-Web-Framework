package org.moeawebframework

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class MoeaWebFrameworkApplication

fun main(args: Array<String>) {
    runApplication<MoeaWebFrameworkApplication>(*args)
}
