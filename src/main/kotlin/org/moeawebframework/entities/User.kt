package org.moeawebframework.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("user_entity")
data class User(

    @Id
    var id: String = ""

)