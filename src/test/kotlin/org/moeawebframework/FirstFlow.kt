package org.moeawebframework


import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.moeawebframework.configs.R2DBCTestConfig
import org.moeawebframework.controllers.arrayStandardCommonStructures
import org.moeawebframework.dto.ArrayStandardCommonStructureDTO
import org.moeawebframework.dto.IDDTO
import org.moeawebframework.dto.NewEvaluationDTO
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(R2DBCTestConfig::class)
class FirstFlow {

    private val webClient = WebClient.builder().baseUrl("http://localhost:8080").build()

    companion object {
        private val environment: KDockerComposeContainer =
            KDockerComposeContainer(File("src/test/resources/compose-test.yml"))
                .apply {
                    withExposedService("percona_1", 3306)
                    withExposedService("keycloak_1", 8180, Wait.forLogMessage(".+Admin console listening on.+", 1))
                    withExposedService("minio_1", 9000)
                    start()
                }
    }

    @Test
    fun testStandardsEndpoint() {
        var response = webClient.get().uri("standards/algorithms").retrieve()
        Assertions.assertEquals(
            arrayStandardCommonStructures["algorithms"],
            response.bodyToMono(ArrayStandardCommonStructureDTO::class.java).block()
        )
        response = webClient.get().uri("standards/problems").retrieve()
        Assertions.assertEquals(
            arrayStandardCommonStructures["problems"],
            response.bodyToMono(ArrayStandardCommonStructureDTO::class.java).block()
        )
        response = webClient.get().uri("standards/referencesets").retrieve()
        Assertions.assertEquals(
            arrayStandardCommonStructures["referencesets"],
            response.bodyToMono(ArrayStandardCommonStructureDTO::class.java).block()
        )
    }

    @Test
    fun testNewEvaluation() = runBlocking {
        val newEvaluation = NewEvaluationDTO("Test evaluation", 10000, 10, 1, 2, 3)
        val response =
            webClient.post().uri("/evaluations").bodyValue(newEvaluation).retrieve().bodyToMono<IDDTO>().block()
        Assertions.assertEquals(true, response?.id!! > 0)
    }

}