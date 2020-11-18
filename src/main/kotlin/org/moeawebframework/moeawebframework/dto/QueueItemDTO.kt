package org.moeawebframework.moeawebframework.dto

data class QueueItemDTO(

    var name: String = "",

    var algorithmMD5: String = "",

    var problemMD5: String = "",

    var referenceSetMD5: String = "",

    var numberOfEvaluations: Int = 0,

    var numberOfSeeds: Int = 0

)