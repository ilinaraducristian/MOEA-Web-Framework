package org.moeawebframework.moeawebframework.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("reference_sets_users")
data class ReferenceSetUser(

    @Id
    var id: Long? = null,

    var userId: Long = 0,

    var referenceSetId: Long = 0

)