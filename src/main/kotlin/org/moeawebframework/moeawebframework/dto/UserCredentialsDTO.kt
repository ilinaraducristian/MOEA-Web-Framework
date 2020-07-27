package org.moeawebframework.moeawebframework.dto

import javax.validation.constraints.NotEmpty

data class UserCredentialsDTO (

    @NotEmpty
    var username: String = "",
    @NotEmpty
    var password: String = ""
)