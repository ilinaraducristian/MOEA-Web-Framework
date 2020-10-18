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

  fun toKeycloakCredentialRepresentation(): String {
    var signupInfoJSON = """{"username": "$username", """
    signupInfoJSON += """"credentials": [{"type": "password", "value": "$password", "temporary": false}], """
    signupInfoJSON += """"email": "$email", """
    signupInfoJSON += """"firstName": "$firstName""""
    if (lastName != null) {
      signupInfoJSON += """, "lastName": "$lastName""""
    }
    signupInfoJSON += "}"
    return signupInfoJSON
  }

}