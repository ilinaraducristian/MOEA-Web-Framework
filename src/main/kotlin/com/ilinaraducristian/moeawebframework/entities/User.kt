package com.ilinaraducristian.moeawebframework.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.NaturalId
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table
data class User(

    @Id
    @GeneratedValue
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    @NotBlank
    @NaturalId
    var username: String = "guest",

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

    @Column(nullable = false, columnDefinition = "VARBINARY(1024)")
    var problems: ArrayList<String> = ArrayList(),

    @Column(nullable = false, columnDefinition = "VARBINARY(1024)")
    var algorithms: ArrayList<String> = ArrayList(),

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnore
    var queue: MutableList<ProblemSolver> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var authorities: MutableList<Authority> = mutableListOf()

) : Serializable