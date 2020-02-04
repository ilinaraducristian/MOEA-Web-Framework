package com.ilinaraducristian.moeawebframework

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.charset.Charset

@SpringBootApplication
class MoeawebframeworkApplication

fun main(args: Array<String>) {
	runApplication<MoeawebframeworkApplication>(*args)
}
