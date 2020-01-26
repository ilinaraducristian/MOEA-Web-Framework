package com.ilinaraducristian.moeawebframework.dto

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class User(

    @Id
    @GeneratedValue
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    @NotBlank
    var username: String = "",

    @Column(nullable = false)
    @NotBlank
    var password: String = "",

    @Column(nullable = false, unique = true)
    @NotBlank
    var email: String = "",

    @Column(nullable = false)
    var firstName: String = "",

    var lastName: String? = null,

    @OneToMany(mappedBy = "user")
    var problems: MutableList<Problem> = ArrayList()

)