package dev.fredag.cheerwithme.model

data class AppleOauthResponse(
    val access_token: String,
    val expires_in: Long,
    val id_token: String,
    val refresh_token: String,
    val token_type: String
)

data class GoogleOauthResponse(
    val access_token: String,
    val expires_in: Long,
    val scope: String,
    val id_token: String,
    val refresh_token: String?,
    val token_type: String
)

data class AppleUserSignInRequest(val code: String)
data class GoogleUserSignInRequest(val code: String)
