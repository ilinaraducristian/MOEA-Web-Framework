package org.moeawebframework.controllers

import org.moeawebframework.dto.ArrayEvaluationDTO
import org.moeawebframework.dto.EvaluationDTO
import org.moeawebframework.dto.IDDTO
import org.moeawebframework.dto.NewEvaluationDTO
import org.moeawebframework.entities.Evaluation
import org.moeawebframework.exceptions.EvaluationNotFoundException
import org.moeawebframework.repositories.EvaluationRepository
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("evaluations")
class EvaluationsController(
    private val evaluationRepository: EvaluationRepository,
) {

    @GetMapping
    suspend fun getEvaluations(authentication: Authentication): ArrayEvaluationDTO {
        val jwt = authentication.principal as Jwt
        val evaluations =
            evaluationRepository.findAllByUserId(jwt.subject).map {
                EvaluationDTO(it)
            }
        return ArrayEvaluationDTO(evaluations)
    }

    @PostMapping
    suspend fun addEvaluation(authentication: Authentication?, @RequestBody newEvaluation: NewEvaluationDTO): IDDTO {
        println("AUTHENTICATION:")
        println(authentication)
        val evaluation = Evaluation(newEvaluation)
        evaluation.user_id = if (authentication != null) (authentication.principal as Jwt).subject else null
        val savedEvaluation = evaluationRepository.save(evaluation)
        return IDDTO(savedEvaluation.id!!)
    }

    @GetMapping("{id}")
    suspend fun getEvaluation(authentication: Authentication?, @PathVariable("id") id: Long): EvaluationDTO {
        return EvaluationDTO(commonEvaluationCheck(authentication, id))
    }

    @DeleteMapping("{id}")
    suspend fun deleteEvaluation(authentication: Authentication, @PathVariable("id") id: Long) {
        val evaluation = evaluationRepository.findById(id)
        if ((evaluation == null) || (evaluation.user_id == null) || (authentication.principal as Jwt).subject != evaluation.user_id)
            throw RuntimeException(EvaluationNotFoundException)
        evaluationRepository.delete(evaluation)
    }

//    TODO guests can delete any other guests evaluations
//    @DeleteMapping("{id}")
//    suspend fun deleteEvaluation(authentication: Authentication?, @PathVariable("id") id: Long) {
//        val evaluation = commonEvaluationCheck(authentication, id)
//        evaluationRepository.delete(evaluation)
//    }

    private suspend fun commonEvaluationCheck(authentication: Authentication?, id: Long): Evaluation {
        val evaluation = evaluationRepository.findById(id)
        if ((evaluation == null) ||
            (authentication == null && evaluation.user_id != null) ||
            (authentication != null && evaluation.user_id == null) ||
            (authentication != null && (authentication.principal as Jwt).subject != evaluation.user_id)
        ) throw RuntimeException(EvaluationNotFoundException)
        return evaluation
    }

}