package org.moeawebframework.repositories

import org.moeawebframework.entities.CommonStructureEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface CommonStructureRepository : R2dbcRepository<CommonStructureEntity, Long> {

    @Query("SELECT `id`, `name`, `md5` FROM `common_structures` WHERE `user_id` = $1 AND `type` = $2")
    suspend fun findAllByUserIdAndType(userId: String, type: Int): List<CommonStructureEntity>

    @Query("SELECT `id`, `user_id`, `name` FROM `common_structures` WHERE `md5` = $1 AND `type` = $2")
    suspend fun findByMd5AndType(md5: String, type: Int): CommonStructureEntity?

    @Query("SELECT `id`, `user_id`, `name` FROM `common_structures` WHERE `id` = $1 AND `type` = $2")
    suspend fun findByIdAndType(id: Long, type: Int): CommonStructureEntity

}