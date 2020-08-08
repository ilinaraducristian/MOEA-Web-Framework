package org.moeawebframework.moeawebframework.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("processes")
data class Process(

    @Id
    var id: Long? = null,

    var name: String = "",

    var numberOfEvaluations: Int = 0,

    var numberOfSeeds: Int = 0,

    var status: String = "waiting",

    var rabbitId: String = "",

    var results: String = "",

    var algorithmSha256: String = "",

    var problemSha256: String = "",

    var referenceSetSha256: String = "",

    var userId: Long? = null


)

