package org.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ArrayEvaluationDTO(

    @JsonProperty("evaluations", required = true)
    var evaluations: List<EvaluationDTO>

)