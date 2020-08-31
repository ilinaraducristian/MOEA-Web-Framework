package org.moeawebframework.moeawebframework.dto

data class ProcessDTO(

    var name: String = "",

    var algorithmSha256: String = "",

    var problemSha256: String = "",

    var referenceSetSha256: String = "",

    var numberOfEvaluations: Int = 0,

    var numberOfSeeds: Int = 0

)