package org.moeawebframework.entities

data class CommonStructureEntity(
    var id: Long? = null,
    var userId: String,
    var type: Int,
    var name: String,
    var md5: String
)

fun commonStructureToInt(commonStructure: String): Int {
    return when (commonStructure) {
        "algorithms" -> 0
        "problems" -> 1
        "referencesets" -> 2
        else -> 0
    }
}