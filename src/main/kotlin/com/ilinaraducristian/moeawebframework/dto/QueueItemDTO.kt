package com.ilinaraducristian.moeawebframework.dto

data class QueueItemDTO(

    var name: String,
    var numberOfEvaluations: Int,
    var numberOfSeeds: Int,
    var problem: String,
    var algorithm: String

)