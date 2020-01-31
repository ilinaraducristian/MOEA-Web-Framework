package com.ilinaraducristian.moeawebframework.dto

import org.hibernate.annotations.NaturalId
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

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
    var firstName: String = "",

    var lastName: String? = null,

    var enabled: Boolean = true,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var problems: List<Problem>? = null,

    @OneToMany(mappedBy = "user")
    var authorities: Set<Authority>? = null

) : Serializable