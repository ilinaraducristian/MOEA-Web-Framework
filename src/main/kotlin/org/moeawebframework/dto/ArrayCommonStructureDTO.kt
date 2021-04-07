package org.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ArrayCommonStructureDTO(

    @JsonProperty("common_structures", required = true)
    var commonStructures: List<CommonStructureDTO>

)