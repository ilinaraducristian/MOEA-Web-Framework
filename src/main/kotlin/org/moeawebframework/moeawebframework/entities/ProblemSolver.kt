package org.moeawebframework.moeawebframework.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("problem_solvers")
data class ProblemSolver(

    @Id
    var id: Long? = null,

    var name: String = "",

    var numberOfEvaluations: Int = 0,

    var numberOfSeeds: Int = 0,

    var status: String = "",

    var rabbitId: String = "",

    var results: String = "",

    var problem: String = "",

    var algorithm: String = "",

    var userId: Long? = null


)