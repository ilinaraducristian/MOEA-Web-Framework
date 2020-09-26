package org.moeawebframework.moeawebframework.dto

import javax.validation.constraints.NotEmpty

data class SignupInfoDTO(

    @NotEmpty
    var username: String = "",

    @NotEmpty
    var password: String = "",

    @NotEmpty
    var email: String = "",

    @NotEmpty
    var firstName: String = "",

    var lastName: String? = null

) {

    fun toJSON(): String {
        return """{"username": "$username", "password": "$password", "email": "$email", "firstName": "$firstName"${lastName == null ?: """, "lastName": "$lastName"""}}"""
    }

}