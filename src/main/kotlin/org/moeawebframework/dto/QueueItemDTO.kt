package org.moeawebframework.dto

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

data class QueueItemDTO(

    @field:NotBlank
    var name: String = "",

    @field:Min(500)
    var numberOfEvaluations: Int = 0,

    @field:Min(1)
    var numberOfSeeds: Int = 0,

    @field:NotBlank
    var algorithmMD5: String = "",

    @field:NotBlank
    var problemMD5: String = "",

    @field:NotBlank
    var referenceSetMD5: String = ""

)