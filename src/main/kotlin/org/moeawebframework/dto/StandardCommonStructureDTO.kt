package org.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class StandardCommonStructureDTO(

    @JsonProperty("id", required = true)
    var id: Long,

    @JsonProperty("name", required = true)
    var name: String
)

