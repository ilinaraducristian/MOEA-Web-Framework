package org.moeawebframework.repositories

import org.moeawebframework.entities.CommonStructure
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CommonStructureRepository : CoroutineCrudRepository<CommonStructure, Long> {

    @Query("SELECT `id`, `name`, `md5` FROM `common_structures` WHERE `user_id` = :userId AND `type` = :type")
    suspend fun findAllByUserIdAndType(userId: String, type: Int): List<CommonStructure>

    @Query("SELECT `id`, `user_id` AS `userId`, `type`, `name`, `md5` FROM `common_structures` c WHERE c.`type` = :type")
    suspend fun findAllStandardsByType(type: Int): List<CommonStructure>

    @Query("SELECT `id`, `user_id`, `name` FROM `common_structures` WHERE `md5` = :md5 AND `type` = :type")
    suspend fun findByMd5AndType(md5: String, type: Int): CommonStructure?

    @Query("SELECT `id`, `user_id`, `name` FROM `common_structures` WHERE `id` = :id AND `type` = :type")
    suspend fun findByIdAndType(id: Long, type: Int): CommonStructure?

}