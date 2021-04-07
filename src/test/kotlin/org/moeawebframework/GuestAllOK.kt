package org.moeawebframework

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.moeawebframework.configs.R2DBCTestConfig
import org.moeawebframework.dto.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.containers.wait.strategy.Wait
import reactor.core.publisher.Mono
import java.io.File

private val standardStructures: ArrayList<StandardCommonStructureDTO> = arrayListOf()
private var evaluationId: Long = -1
private lateinit var newEvaluationDTO: NewEvaluationDTO

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Import(R2DBCTestConfig::class)
class AllOK {

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
    @Order(1)
    fun `test standards endpoint`() {
        val algorithmsMono = webClient.get().uri("standards/algorithms").retrieve()
            .bodyToMono(ArrayStandardCommonStructureDTO::class.java)
        val problemsMono = webClient.get().uri("standards/problems").retrieve()
            .bodyToMono(ArrayStandardCommonStructureDTO::class.java)
        val referenceSetsMono = webClient.get().uri("standards/referencesets").retrieve()
            .bodyToMono(ArrayStandardCommonStructureDTO::class.java)
        Mono.zip(algorithmsMono, problemsMono, referenceSetsMono).doOnSuccess {
            assert(it.t1.commonStructures.isNotEmpty())
            assert(it.t2.commonStructures.isNotEmpty())
            assert(it.t3.commonStructures.isNotEmpty())
            standardStructures.add(it.t1.commonStructures[0])
            standardStructures.add(it.t2.commonStructures[0])
            standardStructures.add(it.t3.commonStructures[0])
        }.block()
    }

    @Test
    @Order(2)
    fun `test new evaluation`() {
        val newEvaluationDTO = NewEvaluationDTO(
            "A new evaluation", 10000, 10,
            standardStructures[0].id, standardStructures[1].id, standardStructures[2].id
        )
        val id =
            webClient.post().uri("evaluations").bodyValue(newEvaluationDTO).retrieve().bodyToMono(IDDTO::class.java)
                .block()?.id!!
        assert(id > 0)
        evaluationId = id
    }

    @Test
    @Order(3)
    fun `test get existing evaluation`() {
        val evaluation =
            webClient.get().uri("evaluations/$evaluationId").retrieve().bodyToMono(EvaluationDTO::class.java).block()
        evaluation?.compareTo(newEvaluationDTO)?.let { assert(it) }
    }

}