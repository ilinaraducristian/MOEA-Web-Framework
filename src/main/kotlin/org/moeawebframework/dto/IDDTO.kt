package org.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class IDDTO(

    @JsonProperty("id", required = true)
    var id: Long
)

