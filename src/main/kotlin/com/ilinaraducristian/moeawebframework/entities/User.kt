package com.ilinaraducristian.moeawebframework.entities

import org.hibernate.annotations.NaturalId
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

@Entity
@Table(name = "users")
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

    @ManyToMany
    @JoinTable(name = "problem_user",
        joinColumns = [JoinColumn(name = "problem_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "user_id",
            referencedColumnName = "id")])
    var problems: MutableSet<Problem> = HashSet(),

    @ManyToMany
    @JoinTable(name = "algorithm_user",
        joinColumns = [JoinColumn(name = "algorithm_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "user_id",
            referencedColumnName = "id")])
    var algorithms: MutableSet<Algorithm> = HashSet(),

    @OneToMany(mappedBy = "user")
    var queue: MutableList<QueueItem> = ArrayList(),

    @OneToMany(mappedBy = "user")
    var authorities: MutableList<Authority> = ArrayList()

) : Serializable