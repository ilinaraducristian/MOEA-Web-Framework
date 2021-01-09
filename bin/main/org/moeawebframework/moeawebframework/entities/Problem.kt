package org.moeawebframework.moeawebframework.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("problems")
data class Problem(

    @Id
    var id: Long? = null,

    var name: String = "",

    var md5: String = ""

)