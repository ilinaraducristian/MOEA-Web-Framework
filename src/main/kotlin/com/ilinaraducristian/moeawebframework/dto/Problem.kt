package com.ilinaraducristian.moeawebframework.dto

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class Problem(

    @Id
    @GeneratedValue
    var id: Long,

    @Column(nullable = false)
    @NotBlank
    var name: String,

    @Column(nullable = false)
    @NotBlank
    var algorithm: String,

    @Column(nullable = false)
    @NotBlank
    var status: String,

    var results: ArrayList<QualityIndicators>?,

    @Column(nullable = false)
    var numberOfEvaluations: Int = 10000,

    @Column(nullable = false)
    var numberOfSeeds: Int = 10,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User = User()

)