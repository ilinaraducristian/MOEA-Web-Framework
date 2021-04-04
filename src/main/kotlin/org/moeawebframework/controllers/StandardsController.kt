package org.moeawebframework.controllers

import org.moeawebframework.dto.ArrayStandardCommonStructureDTO
import org.moeawebframework.dto.StandardCommonStructureDTO
import org.moeawebframework.entities.commonStructureToInt
import org.moeawebframework.repositories.CommonStructureRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("standards")
class StandardsController(
    private val commonStructureRepository: CommonStructureRepository
) {

    @GetMapping("{commonStructure}")
    suspend fun getStandardCommonStructures(@PathVariable("commonStructure") commonStructure: String): ArrayStandardCommonStructureDTO {
        return ArrayStandardCommonStructureDTO(commonStructureRepository.findAllStandardsByType(
            commonStructureToInt(
                commonStructure
            )
        )
            .map { StandardCommonStructureDTO(it.id!!, it.name) })
    }

}