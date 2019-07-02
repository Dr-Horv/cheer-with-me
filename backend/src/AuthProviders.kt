package dev.fredag.cheerwithme

import io.ktor.auth.OAuthServerSettings
import io.ktor.http.HttpMethod

val loginProviders = listOf(
    OAuthServerSettings.OAuth2ServerSettings(
        name = "google",
        authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
        accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
        requestMethod = HttpMethod.Post,
        clientId = System.getenv("GOOGLE_CLIENT_ID"),
        clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
        defaultScopes = listOf("profile")
    )
).associateBy { it.name }