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

    var problems: ArrayList<String> = ArrayList(),

    var algorithms: ArrayList<String> = ArrayList(),

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var queue: MutableList<QueueItem> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var authorities: MutableList<Authority> = mutableListOf()

) : Serializable