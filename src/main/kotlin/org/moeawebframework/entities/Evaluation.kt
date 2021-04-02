package org.moeawebframework.entities

import org.moeawebframework.dto.NewEvaluationDTO
import org.moeawebframework.dto.QualityIndicatorsDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("evaluations")
data class Evaluation(

    @Id
    var id: Long? = null,

    var user_id: String? = null,

    var name: String = "",

    var nfe: Int = 0,

    var seeds: Int = 0,

    var algorithm_id: Long = 0,

    var problem_id: Long = 0,

    var reference_set_id: Long = 0,

    var status: String = "created",

    var results: QualityIndicatorsDTO? = null

) {

    constructor(newEvaluation: NewEvaluationDTO) : this(
        null,
        null,
        newEvaluation.name,
        newEvaluation.nfe,
        newEvaluation.seeds,
        newEvaluation.algorithmId,
        newEvaluation.problemId,
        newEvaluation.referenceSetId
    )

}