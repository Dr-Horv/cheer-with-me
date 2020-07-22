package dev.fredag.cheerwithme.service

import com.fasterxml.jackson.module.kotlin.readValue
import dev.fredag.cheerwithme.logger
import dev.fredag.cheerwithme.objectMapper
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.http.Parameters

data class Oauth2Parameters(
    val grantType: String,
    val code: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String?
)

class Oauth2Service {
    val log by logger();

    suspend inline fun <reified T> authenticate(tokenUrl: String, parameters: Oauth2Parameters): T {
        val data = Parameters.build {
            append("grant_type", parameters.grantType)
            append("code", parameters.code)
            parameters.redirectUri?.apply { append("redirect_uri", parameters.redirectUri) }
            append("client_id", parameters.clientId)
            append("client_secret", parameters.clientSecret
            )
        }


        log.debug("Sending form with data: $data")
        val body = HttpClient().submitForm<String>(
            url = tokenUrl,
            formParameters = data,
            encodeInQuery = false,
            block = { header("user-agent", "cheer-with-me") }
        )

        log.debug(body)
        return objectMapper.readValue<T>(body)
    }
}