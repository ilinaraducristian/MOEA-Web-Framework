package org.moeawebframework.moeawebframework.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(

    @Id
    var id: Long? = null,
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String? = null

//    var id: Long = 0,
//
//    @Column(nullable = false, unique = true)
//    @NotBlank
//    @NaturalId
//    var username: String = "guest",
//
//    @Column(nullable = false)
//    @NotBlank
//    var password: String = "",
//
//    @Column(nullable = false, unique = true)
//    @NotBlank
//    var email: String = "",
//
//    @Column(nullable = false)
//    @NotBlank
//    var firstName: String = "",
//
//    var lastName: String? = null,
//
//    var enabled: Boolean = true,
//
//    @Column(nullable = false, columnDefinition = "VARBINARY(1024)")
//    var problems: ArrayList<String> = ArrayList(),
//
//    @Column(nullable = false, columnDefinition = "VARBINARY(1024)")
//    var algorithms: ArrayList<String> = ArrayList(),
//
//    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
//    @JsonIgnore
//    var queue: MutableList<ProblemSolver> = mutableListOf(),
//
//    @OneToMany(mappedBy = "user")
//    @JsonIgnore
//    var authorities: MutableList<Authority> = mutableListOf()

)