package org.moeawebframework.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("common_structures")
data class CommonStructure(
    @Id
    var id: Long? = null,
    var userId: String? = null,
    var type: Int,
    var name: String,
    var md5: String
)

fun commonStructureToInt(commonStructure: String): Int {
    return when (commonStructure) {
        "algorithms" -> 1
        "problems" -> 2
        "referencesets" -> 3
        else -> 0
    }
}