package org.moeawebframework.repositories

import org.moeawebframework.entities.Evaluation
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository


interface EvaluationRepository : CoroutineCrudRepository<Evaluation, Long> {

    @Query("SELECT EXISTS (SELECT 1 FROM `algorithms` WHERE `user_id` = $1 AND `md5` = $2)")
    suspend fun existsByUserIdAAndMd5(userId: String, md5: String): Boolean

    @Query("SELECT `id`, `name`, `nfe`, `seeds`, `algorithm_id`, `problem_id`, `reference_set_id`, `status`, `results` FROM `evaluations` WHERE `user_id` = :userId")
    suspend fun findAllByUserId(userId: String): List<Evaluation>

}