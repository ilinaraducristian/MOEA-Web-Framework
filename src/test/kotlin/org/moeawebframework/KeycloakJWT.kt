package org.moeawebframework

import com.fasterxml.jackson.annotation.JsonProperty

data class KeycloakJWT(
    @JsonProperty("access_token", required = true)
    var accessToken: String = "",
    @JsonProperty("expires_in", required = true)
    var expiresIn: Int = 0,
    @JsonProperty("refresh_expires_in", required = true)
    var refreshExpiresIn: Int = 0,
    @JsonProperty("refresh_token", required = true)
    var refreshToken: String = "",
    @JsonProperty("token_type", required = true)
    var tokenType: String = "",
    @JsonProperty("not-before-policy", required = true)
    var notBeforePolicy: Int = 0,
    @JsonProperty("session_state", required = true)
    var sessionState: String = "",
    @JsonProperty("scope", required = true)
    var scope: String = ""

)