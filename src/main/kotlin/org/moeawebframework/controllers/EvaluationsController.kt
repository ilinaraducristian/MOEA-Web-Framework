package org.moeawebframework.controllers

import org.moeawebframework.dao.EvaluationsDAO
import org.moeawebframework.dto.ArrayEvaluationDTO
import org.moeawebframework.dto.EvaluationModelDTO
import org.moeawebframework.dto.IDDTO
import org.moeawebframework.dto.NewEvaluationDTO
import org.moeawebframework.entities.Evaluation
import org.moeawebframework.exceptions.EvaluationCouldNotBeCreated
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("evaluations")
class EvaluationsController(
    private val evaluationRepository: EvaluationsDAO,
) {


    @GetMapping
    suspend fun getEvaluations(authentication: Authentication): ArrayEvaluationDTO {
        val jwt = authentication.principal as Jwt
        val evaluations =
            evaluationRepository.getALlByUserId(jwt.subject).map {
                EvaluationModelDTO(it)
            }
        return ArrayEvaluationDTO(evaluations)
    }

    @PostMapping
    suspend fun addEvaluation(authentication: Authentication?, @RequestBody newEvaluation: NewEvaluationDTO): IDDTO {
        val evaluation = Evaluation(newEvaluation)
        evaluation.user_id = if (authentication != null) (authentication.principal as Jwt).subject else null
        val savedEvaluation = evaluationRepository.save(evaluation) ?: throw RuntimeException(
            EvaluationCouldNotBeCreated
        )
        return IDDTO(savedEvaluation.id!!)
    }

    @GetMapping("{id}")
    suspend fun getEvaluation(authentication: Authentication?, @PathVariable("id") id: Long): EvaluationModelDTO {
        val evaluation =
            evaluationRepository.getById(id) ?: throw RuntimeException("Evaluation not found")
        if (authentication == null && evaluation.user_id != null) throw RuntimeException("Evaluation not found")
        if (authentication != null && evaluation.user_id == null) throw RuntimeException("Evaluation not found")
        if (authentication != null && (authentication.principal as Jwt).subject != evaluation.user_id) throw RuntimeException(
            "Evaluation not found"
        )
        return EvaluationModelDTO(evaluation)
    }

    @DeleteMapping("{id}")
    suspend fun deleteEvaluation(authentication: Authentication?, @PathVariable("id") id: Long): EvaluationModelDTO {
        val evaluation =
            evaluationRepository.getById(id) ?: throw RuntimeException("Evaluation not found")
        if (authentication == null && evaluation.user_id != null) throw RuntimeException("Evaluation not found")
        if (authentication != null && evaluation.user_id == null) throw RuntimeException("Evaluation not found")
        if (authentication != null && (authentication.principal as Jwt).subject != evaluation.user_id) throw RuntimeException(
            "Evaluation not found"
        )
        evaluationRepository.delete(evaluation)
        return EvaluationModelDTO(evaluation)
    }

}