package org.moeawebframework.dao

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.moeawebframework.entities.Evaluation
import org.moeawebframework.repositories.EvaluationRepository
import org.springframework.stereotype.Repository

@Repository
class EvaluationsDAO(
    private val evaluationRepository: EvaluationRepository
) {

    suspend fun save(commonStructure: Evaluation): Evaluation? {
        return evaluationRepository.save(commonStructure).awaitFirstOrNull()
    }

    suspend fun getALlByUserId(userId: String): List<Evaluation> {
        return evaluationRepository.findAllByUserId(userId)
    }

    suspend fun getById(id: Long): Evaluation? {
        return evaluationRepository.findById(id).awaitFirstOrNull()
    }

    suspend fun delete(commonStructure: Evaluation) {
        evaluationRepository.delete(commonStructure).awaitFirstOrNull()
            ?: throw RuntimeException("[500] Error when deleting Common Structure")
    }

}