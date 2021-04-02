package org.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.moeawebframework.entities.CommonStructureEntity
import org.moeawebframework.repositories.CommonStructureRepository
import org.springframework.stereotype.Repository

@Repository
class CommonStructureDAO(
    private val commonStructureRepository: CommonStructureRepository
) {

    suspend fun save(commonStructure: CommonStructureEntity): CommonStructureEntity? {
        return commonStructureRepository.save(commonStructure).awaitFirstOrNull()
    }

    suspend fun getALlByUserIdAndType(userId: String, type: Int): List<CommonStructureEntity> {
        return commonStructureRepository.findAllByUserIdAndType(userId, type)
    }

    suspend fun getByIdAndType(id: Long, type: Int): CommonStructureEntity? {
        return commonStructureRepository.findByIdAndType(id, type)
    }

    suspend fun getByMd5AndType(md5: String, type: Int): CommonStructureEntity? {
        return commonStructureRepository.findByMd5AndType(md5, type)
    }

    suspend fun delete(commonStructure: CommonStructureEntity) {
        commonStructureRepository.delete(commonStructure).awaitFirstOrNull()
            ?: throw RuntimeException("[500] Error when deleting Common Structure")
    }

}