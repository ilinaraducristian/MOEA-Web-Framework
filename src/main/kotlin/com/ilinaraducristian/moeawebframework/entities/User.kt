package com.ilinaraducristian.moeawebframework.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.NaturalId
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.MutableSet

@Entity
@Table
data class User(

    @Id
    @GeneratedValue
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    @NotBlank
    @NaturalId
    var username: String = "",

    @Column(nullable = false)
    @NotBlank
    var password: String = "",

    @Column(nullable = false, unique = true)
    @NotBlank
    var email: String = "",

    @Column(nullable = false)
    @NotBlank
    var firstName: String = "",

    var lastName: String? = null,

    var enabled: Boolean = true,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(name = "user_problem",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "problem_id",
            referencedColumnName = "id")])
    @JsonIgnore
    var problems: MutableSet<Problem> = mutableSetOf(),

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(name = "user_algorithm",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "algorithm_id",
            referencedColumnName = "id")])
    @JsonIgnore
    var algorithms: MutableSet<Algorithm> = mutableSetOf(),

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var queue: MutableList<QueueItem> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var authorities: MutableList<Authority> = mutableListOf()

) : Serializable {
  fun addProblem(problem: Problem) {
    this.problems.add(problem)
    problem.users.add(this)
  }

  fun addAlgorithm(algorithm: Algorithm) {
    algorithm.users.add(this)
    this.algorithms.add(algorithm)
  }

  override fun hashCode(): Int {
    return Objects.hash(id)
  }
}