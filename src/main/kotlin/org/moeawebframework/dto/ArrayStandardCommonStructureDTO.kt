package org.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ArrayStandardCommonStructureDTO(

    @JsonProperty("common_structures", required = true)
    var commonStructures: List<StandardCommonStructureDTO>

)