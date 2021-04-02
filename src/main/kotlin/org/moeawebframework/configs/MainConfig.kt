package org.moeawebframework.configs

import org.moeawebframework.MinioAdapter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.util.MimeTypeUtils

val standardAlgorithms = listOf(
    "CMA-ES",
    "NSGAII",
    "NSGAIII",
    "GDE3",
    "eMOEA",
    "eNSGAII",
    "MOEAD",
    "MSOPS",
    "SPEA2",
    "PAES",
    "PESA2",
    "OMOPSO",
    "SMPSO",
    "IBEA",
    "SMS-EMOA",
    "VEGA",
    "DBEA",
    "RVEA",
    "RSO"
)
val standardProblems = listOf(
    "Belegundu",
    "DTLZ1_2",
    "DTLZ2_2",
    "DTLZ3_2",
    "DTLZ4_2",
    "DTLZ7_2",
    "ROT_DTLZ1_2",
    "ROT_DTLZ2_2",
    "ROT_DTLZ3_2",
    "ROT_DTLZ4_2",
    "ROT_DTLZ7_2",
    "UF1",
    "UF2",
    "UF3",
    "UF4",
    "UF5",
    "UF6",
    "UF7",
    "UF8",
    "UF9",
    "UF10",
    "UF11",
    "UF12",
    "UF13",
    "CF1",
    "CF2",
    "CF3",
    "CF4",
    "CF5",
    "CF6",
    "CF7",
    "CF8",
    "CF9",
    "CF10",
    "LZ1",
    "LZ2",
    "LZ3",
    "LZ4",
    "LZ5",
    "LZ6",
    "LZ7",
    "LZ8",
    "LZ9",
    "WFG1_2",
    "WFG2_2",
    "WFG3_2",
    "WFG4_2",
    "WFG5_2",
    "WFG6_2",
    "WFG7_2",
    "WFG8_2",
    "WFG9_2",
    "ZDT1",
    "ZDT2",
    "ZDT3",
    "ZDT4",
    "ZDT5",
    "ZDT6",
    "Binh",
    "Binh2",
    "Binh3",
    "Binh4",
    "Fonseca",
    "Fonseca2",
    "Jimenez",
    "Kita",
    "Kursawe",
    "Laumanns",
    "Lis",
    "Murata",
    "Obayashi",
    "OKA1",
    "OKA2",
    "Osyczka",
    "Osyczka2",
    "Poloni",
    "Quagliarella",
    "Rendon",
    "Rendon2",
    "Schaffer",
    "Schaffer2",
    "Srinivas",
    "Tamaki",
    "Tanaka",
    "Viennet",
    "Viennet2",
    "Viennet3",
    "Viennet4"
)

@Configuration
class MainConfig {

    @Value("\${rsocket_url:}")
    lateinit var rsocket_url: String

    @Value("\${endpoint:}")
    lateinit var endpoint: String

    @Value("\${access_key:}")
    lateinit var access_key: String

    @Value("\${secret_key:}")
    lateinit var secret_key: String

    @Value("\${bucket:}")
    lateinit var bucket: String

    @Bean
    @Profile("!test")
    fun rSocketRequester(): RSocketRequester {
        val values = rsocket_url.split(":")
        return RSocketRequester
            .builder()
            .rsocketStrategies(
                RSocketStrategies.builder()
                    .encoder(Jackson2JsonEncoder())
                    .decoder(Jackson2JsonDecoder())
                    .build()
            )
            .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
            .tcp(values[0], Integer.parseInt(values[1]))
    }

    @Bean
    fun minioAdapter(): MinioAdapter {
        return MinioAdapter(endpoint, access_key, secret_key, bucket)
    }

}