package org.moeawebframework.moeawebframework.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("reference_sets")
data class ReferenceSet(

    @Id
    var id: Long? = null,

    var name: String = "",

    var sha256: String = ""

)