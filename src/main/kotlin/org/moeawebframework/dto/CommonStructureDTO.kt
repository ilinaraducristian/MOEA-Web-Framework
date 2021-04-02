package org.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CommonStructureDTO(

    @JsonProperty("id", required = true)
    var id: Long,

    @JsonProperty("name", required = true)
    var name: String,

    @JsonProperty("md5", required = true)
    var md5: String
)

