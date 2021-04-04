package org.moeawebframework.controllers

import org.moeawebframework.MinioAdapter
import org.moeawebframework.dto.ArrayCommonStructureDTO
import org.moeawebframework.dto.CommonStructureDTO
import org.moeawebframework.dto.IDDTO
import org.moeawebframework.entities.commonStructureToInt
import org.moeawebframework.exceptions.CommonStructureDoesNotExist
import org.moeawebframework.exceptions.CommonStructureExists
import org.moeawebframework.exceptions.CommonStructureExistsAccessDenied
import org.moeawebframework.repositories.CommonStructureRepository
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.nio.charset.Charset
import java.security.MessageDigest

@RestController
@RequestMapping("commonstructures")
class CommonStructuresController(
    private val commonStructureRepository: CommonStructureRepository,
    private val minioAdapter: MinioAdapter
) {

    @GetMapping("{commonStructure}")
    suspend fun getCommonStructure(
        authentication: Authentication,
        @PathVariable("commonStructure") commonStructure: String
    ): ArrayCommonStructureDTO {
        val jwt = authentication.principal as Jwt
        val arrayCommonStructure =
            commonStructureRepository.findAllByUserIdAndType(jwt.subject, commonStructureToInt(commonStructure))
                .map { CommonStructureDTO(it.id!!, it.name, it.md5) }
        return ArrayCommonStructureDTO(arrayCommonStructure)
    }

    @PostMapping("{commonStructure}")
    suspend fun addCommonStrucure(
        authentication: Authentication,
        @PathVariable("commonStructure") commonStructureType: String,
        @RequestParam("name") name: String,
        @RequestParam("file") file: Resource
    ): IDDTO {
        val jwt = authentication.principal as Jwt
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.update(file.inputStream.readBytes())
        val md5 = messageDigest.digest().toString(Charset.forName("UTF-8"))
        val commonStructure = commonStructureRepository.findByMd5AndType(md5, commonStructureToInt(commonStructureType))
        if (commonStructure != null) {
            throw RuntimeException(CommonStructureExists)
        }

        minioAdapter.upload(md5, file) // TODO if error throw
        val newCommonStructure =
            org.moeawebframework.entities.CommonStructure(
                userId = jwt.subject,
                type = commonStructureToInt(commonStructureType),
                name = name,
                md5 = md5
            )
        val savedCommonStructure = commonStructureRepository.save(newCommonStructure)
        return IDDTO(savedCommonStructure.id!!)
    }

    @GetMapping("{commonStructure}/{id}")
    suspend fun getCommonStructure(
        authentication: Authentication,
        @PathVariable("commonStructure") commonStructureType: String,
        @PathVariable("id") id: Long
    ): CommonStructureDTO {
        val jwt = authentication.principal as Jwt
        val commonStructure =
            commonStructureRepository.findByIdAndType(id, commonStructureToInt(commonStructureType))
                ?: throw RuntimeException(
                    CommonStructureDoesNotExist
                )
        if (commonStructure.userId != jwt.subject) throw RuntimeException(CommonStructureExistsAccessDenied)

        return CommonStructureDTO(commonStructure.id!!, commonStructure.name, commonStructure.md5)
    }

    @GetMapping("{commonStructure}/{id}/download")
    suspend fun getCommonStructureFile(
        authentication: Authentication,
        @PathVariable("commonStructure") commonStructureType: String,
        @PathVariable("id") id: Long
    ): Resource {
        val jwt = authentication.principal as Jwt
        val commonStructure =
            commonStructureRepository.findByIdAndType(id, commonStructureToInt(commonStructureType))
                ?: throw RuntimeException(
                    CommonStructureDoesNotExist
                )
        if (commonStructure.userId != jwt.subject) throw RuntimeException(CommonStructureExistsAccessDenied)
        return ByteArrayResource(minioAdapter.download(commonStructure.md5))
    }

    @DeleteMapping("{commonStructureType}/{id}")
    suspend fun deleteCommonStructure(
        authentication: Authentication,
        @PathVariable("commonStructureType") commonStructureType: String,
        @PathVariable("id") id: Long
    ) {
        val jwt = authentication.principal as Jwt
        val commonStructure =
            commonStructureRepository.findByIdAndType(id, commonStructureToInt(commonStructureType))
                ?: throw RuntimeException(
                    CommonStructureDoesNotExist
                )
        if (commonStructure.userId != jwt.subject) throw RuntimeException(CommonStructureExistsAccessDenied)
        commonStructureRepository.delete(commonStructure)
    }

}