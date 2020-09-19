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

    var lastName: String = ""

)