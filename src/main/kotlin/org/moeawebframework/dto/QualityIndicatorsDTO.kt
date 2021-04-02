package org.moeawebframework.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class QualityIndicatorsDTO(

    @JsonProperty("r1Indicator", required = true)
    var r1Indicator: List<List<Float>> = listOf(),

    @JsonProperty("r2Indicator", required = true)
    var r2Indicator: List<List<Float>> = listOf(),

    @JsonProperty("r3Indicator", required = true)
    var r3Indicator: List<List<Float>> = listOf(),

    @JsonProperty("igd", required = true)
    var igd: List<List<Float>> = listOf()

)

