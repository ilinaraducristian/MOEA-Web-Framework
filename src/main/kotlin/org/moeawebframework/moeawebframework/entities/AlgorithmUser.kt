package org.moeawebframework.moeawebframework.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("algorithms_users")
data class AlgorithmUser (

    @Id
    var id: Long? = null,

    var userId: Long = 0,

    var algorithmId: Long = 0

)