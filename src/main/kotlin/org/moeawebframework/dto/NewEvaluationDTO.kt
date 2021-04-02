package org.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NewEvaluationDTO(

    @JsonProperty("name", required = true)
    var name: String,

    @JsonProperty("nfe", required = true)
    var nfe: Int,

    @JsonProperty("seeds", required = true)
    var seeds: Int,

    @JsonProperty("algorithm_id", required = true)
    var algorithmId: Long,

    @JsonProperty("problem_id", required = true)
    var problemId: Long,

    @JsonProperty("reference_set_id", required = true)
    var referenceSetId: Long
)