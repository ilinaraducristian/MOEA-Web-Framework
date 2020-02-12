package com.ilinaraducristian.moeawebframework.entities

import com.ilinaraducristian.moeawebframework.dto.QualityIndicators
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import kotlin.collections.ArrayList

@Entity
data class QueueItem(
    @Id
    @GeneratedValue
    var id: Long = 0,

    @Column(nullable = false)
    @NotBlank
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

    @Column(nullable = true)
    var solverId: Optional<String> = Optional.empty(),

    @ManyToOne
    @JoinColumn(name = "problem_id")
    var problem: Problem = Problem(),

    @ManyToOne
    @JoinColumn(name = "algorithm_id")
    var algorithm: Algorithm = Algorithm(),

    @Column(nullable = false)
    var results: ArrayList<QualityIndicators> = ArrayList(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User = User()
)