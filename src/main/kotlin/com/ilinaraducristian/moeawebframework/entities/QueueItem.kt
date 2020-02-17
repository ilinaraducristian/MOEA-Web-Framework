package com.ilinaraducristian.moeawebframework.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.ilinaraducristian.moeawebframework.dto.QualityIndicators
import org.hibernate.annotations.Type
import java.io.Serializable
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
    var solverId: String? = null,

    @Column(nullable = false, columnDefinition = "MEDIUMBLOB")
    var results: ArrayList<QualityIndicators> = ArrayList(),

    @ManyToOne
    @JoinColumn(name = "problem_id")
    var problem: Problem = Problem(),

    @ManyToOne
    @JoinColumn(name = "algorithm_id")
    var algorithm: Algorithm = Algorithm(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    var user: User = User()
): Serializable {
    override fun toString(): String {
        return "QueueItem(id=$id, name='$name', numberOfEvaluations=$numberOfEvaluations, numberOfSeeds=$numberOfSeeds, status='$status', rabbitId='$rabbitId', solverId=$solverId, results=$results, problem=$problem, algorithm=$algorithm, user=$user)"
    }
}