package org.moeawebframework.controllers

import org.moeawebframework.configs.standardAlgorithms
import org.moeawebframework.configs.standardProblems
import org.moeawebframework.dto.ArrayStandardCommonStructureDTO
import org.moeawebframework.dto.StandardCommonStructureDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

val arrayStandardAlgorithms =
    ArrayStandardCommonStructureDTO(standardAlgorithms.mapIndexed { index, s -> StandardCommonStructureDTO(index, s) })

val arrayStandardProblems =
    ArrayStandardCommonStructureDTO(standardProblems.mapIndexed { index, s -> StandardCommonStructureDTO(index, s) })

val arrayStandardReferenceSets = arrayStandardProblems

val arrayStandardCommonStructures = mapOf(
    Pair("algorithms", arrayStandardAlgorithms),
    Pair("problems", arrayStandardProblems),
    Pair("referencesets", arrayStandardReferenceSets)
)

@RestController
@RequestMapping("standards")
class StandardsController {

    @GetMapping("{commonStructure}")
    suspend fun getStandardCommonStructures(@PathVariable("commonStructure") commonStructure: String): ArrayStandardCommonStructureDTO {
        return arrayStandardCommonStructures[commonStructure]
            ?: throw RuntimeException("common structure must be algorithms, problems or referencesets")
    }

}