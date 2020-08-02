package org.moeawebframework.moeawebframework.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("problems_users")
data class ProblemUser(

    @Id
    var id: Long? = null,

    var userId: Long = 0,

    var problemId: Long = 0

)