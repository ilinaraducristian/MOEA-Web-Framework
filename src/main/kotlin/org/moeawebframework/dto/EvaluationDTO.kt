package org.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.moeawebframework.entities.Evaluation
import javax.validation.Valid

data class EvaluationDTO(

    @JsonProperty("id", required = true)
    val id: Long,

    @JsonProperty("name", required = true)
    val name: String,

    @JsonProperty("nfe", required = true)
    val nfe: Int,

    @JsonProperty("seeds", required = true)
    val seeds: Int,

    @JsonProperty("algorithm_id", required = true)
    val algorithmId: Long,

    @JsonProperty("problem_id", required = true)
    val problemId: Long,

    @JsonProperty("reference_set_id", required = true)
    val referenceSetId: Long,

    @JsonProperty("status", required = true)
    val status: String,

    @Valid
    @JsonProperty("results", required = true)
    val results: QualityIndicatorsDTO?

) {

    constructor(evaluation: Evaluation) : this(
        evaluation.id!!,
        evaluation.name,
        evaluation.nfe,
        evaluation.seeds,
        evaluation.algorithm_id,
        evaluation.problem_id,
        evaluation.reference_set_id,
        evaluation.status,
        evaluation.results
    )

    fun compareTo(newEvaluationDTO: NewEvaluationDTO): Boolean {
        return newEvaluationDTO.name == this.name &&
                newEvaluationDTO.nfe == this.nfe &&
                newEvaluationDTO.seeds == this.seeds &&
                newEvaluationDTO.algorithmId == this.algorithmId &&
                newEvaluationDTO.problemId == this.problemId &&
                newEvaluationDTO.referenceSetId == this.referenceSetId
    }

}

