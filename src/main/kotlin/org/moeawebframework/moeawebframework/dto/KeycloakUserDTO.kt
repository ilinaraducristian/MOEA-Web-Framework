package org.moeawebframework.moeawebframework.dto

data class KeycloakUserDTO(

    val id: String,

    val createdTimestamp: Long,

    val username: String,

    val enabled: Boolean,

    val totp: Boolean,

    val emailVerified: Boolean,

    val firstName: String,

    val lastName: String,

    val email: String,

    val disableableCredentialTypes: List<String>,

    val requiredActions: List<String>,

    val notBefore: Int,

    val access: Access

)

data class Access(

    val manageGroupMembership: Boolean,

    val view: Boolean,

    val mapRoles: Boolean,

    val impersonate: Boolean,

    val manage: Boolean

)