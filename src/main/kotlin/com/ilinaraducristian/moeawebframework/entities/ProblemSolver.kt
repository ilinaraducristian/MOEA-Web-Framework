package com.ilinaraducristian.moeawebframework.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.ilinaraducristian.moeawebframework.constraints.ProblemSolverNameConstraint
import com.ilinaraducristian.moeawebframework.moea.QualityIndicators
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class ProblemSolver(
    @Id
    @GeneratedValue
    var id: Long = 0,

    @Column(nullable = false)
    @NotBlank
    @ProblemSolverNameConstraint
    var name: String = "",

    @Column(nullable = false)
    var numberOfEvaluations: Int = 10000,

    @Column(nullable = false)
    var numberOfSeeds: Int = 10,

    @Column(nullable = false)
    @NotBlank
    var status: String = "waiting",

    @Column(nullable = false)
    @NotBlank
    var rabbitId: String = "",

    @Column(nullable = false, columnDefinition = "MEDIUMBLOB")
    var results: ArrayList<QualityIndicators> = ArrayList(),

    @Column(nullable = false)
    var problem: String = "",

    @Column(nullable = false)
    var algorithm: String = "",

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    var user: User = User()
) : Serializable