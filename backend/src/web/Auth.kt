package dev.fredag.cheerwithme.web

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

data class CheerWithMePrincipal(val userId: Long): Principal

class CheerWithMeAuthenticationProvider(configuration: Configuration) : AuthenticationProvider(configuration) {
    internal val authenticationFunction = configuration.authenticationFunction

    class Configuration internal constructor(name: String?) : AuthenticationProvider.Configuration(name) {
        internal var authenticationFunction: AuthenticationFunction<String> = {
            throw NotImplementedError(
                "verifyToken function is not specified. Use basic { verifyToken { ... } } to fix."
            )
        }

        /**
         * Sets a validation function that will check given [String] token and return [Principal],
         * or null if credential does not correspond to an authenticated principal
         */
        fun verifyToken(token: suspend ApplicationCall.(String) -> Principal?) {
            authenticationFunction = token
        }
    }

}

fun Authentication.Configuration.cheerWithMe(
    name: String? = null,
    configure: CheerWithMeAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = CheerWithMeAuthenticationProvider(CheerWithMeAuthenticationProvider.Configuration(name).apply(configure))
    val verify = provider.authenticationFunction
    provider.pipeline.intercept(AuthenticationPipeline.CheckAuthentication) { context ->
        val authHeader = context.call.request.headers["authorization"]
        if (authHeader == null || !authHeader.contains("bearer ", true)) {
            call.respond(HttpStatusCode.Unauthorized)
            context.challenge("cheerWithMeAuth", AuthenticationFailedCause.NoCredentials) {
                call.respond(UnauthorizedResponse())
                it.complete()
            }
        } else {
            val token = authHeader.substring("bearer ".length)
            val principal = verify(call, token)
            if (principal != null) {
                context.principal(principal)
            } else {
                context.challenge("cheerWithMeAuth", AuthenticationFailedCause.InvalidCredentials) {
                    call.respond(UnauthorizedResponse())
                    it.complete()
                }
            }
        }
    }
    register(provider)
}
