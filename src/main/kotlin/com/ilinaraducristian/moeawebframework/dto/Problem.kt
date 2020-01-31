package com.ilinaraducristian.moeawebframework.dto

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "problems")
data class Problem(

    @Id
    @GeneratedValue
    var id: Long = 0,

    @Column(nullable = false)
    @NotBlank
    var userDefinedName: String = "",

    @Column(nullable = false)
    @NotBlank
    var name: String = "",

    @Column(nullable = false)
    @NotBlank
    var algorithm: String = "",

    @Column(nullable = false)
    @NotBlank
    var status: String = "",

    var results: ArrayList<QualityIndicators>? = null,

    @Column(nullable = false)
    var numberOfEvaluations: Int = 10000,

    @Column(nullable = false)
    var numberOfSeeds: Int = 10,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = User(),

    var enabled: Boolean = true

) : Serializable