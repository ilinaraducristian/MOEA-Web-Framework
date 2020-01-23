package com.ilinaraducristian.moeawebframework.dto

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
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

    @NotBlank
    var results: ArrayList<QualityIndicators>?,

    @Column(nullable = false)
    var numberOfEvaluations: Int = 10000,

    @Column(nullable = false)
    var numberOfSeeds: Int = 10

): Serializable