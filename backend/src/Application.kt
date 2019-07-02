package dev.fredag.cheerwithme

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import dev.fredag.cheerwithme.service.Database
import dev.fredag.cheerwithme.service.initAwsSdkClients
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.host
import io.ktor.request.path
import io.ktor.request.port
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import org.slf4j.event.Level


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
            throw cause
        }
    }
    install(DefaultHeaders)

    val authOauthForLogin = "authOauthForLogin"
    install(Authentication) {
        oauth(authOauthForLogin) {
            client = HttpClient()
            providerLookup = {
                val path = this.request.path()
                val type = this.parameters["type"]
                if (path.startsWith("/login") && type != null) {
                    loginProviders[type]
                } else {
                    loginProviders["google"]
                }
            }
            urlProvider = { redirectUrl("/login") }
        }
    }

    Database.init()
    initAwsSdkClients()
    install(Routing) {
        get("/") {
            call.respondText("Cheers mate! :D", contentType = ContentType.Text.Plain)
        }

        get("/health") {
            call.respond(
                mapOf("status" to "UP")
            )
        }

        post("/echo") {
            val body = call.receive<Map<String, Any>>()
            println(body)
            call.respond(body)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        authenticate(authOauthForLogin) {
            route("/login") {
                param("error") {
                    handle {
                        call.loginFailedPage(call.parameters.getAll("error").orEmpty())
                    }
                }

                handle {
                    val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                        ?: error("No principal")

                    val json = HttpClient().get<String>("https://www.googleapis.com/userinfo/v2/me") {
                        header("Authorization", "Bearer ${principal.accessToken}")
                    }

                    val data = ObjectMapper().readValue<Map<String, Any?>>(json)
                    val id = data["id"] as String?
                    println(id)
                    println(data)
                    call.loggedInSuccessResponse(principal)
                }
            }

            get("/safe") {
                call.respond(mapOf("secret" to "hello"))
            }
        }
    }
}

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443
    val hostPort = request.host() + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = request.origin.scheme
    return "$protocol://$hostPort$path"
}

private suspend fun ApplicationCall.loginPage() {
    respondText { "Yeah, login page" }
}

private suspend fun ApplicationCall.loginFailedPage(errors: List<String>) {
    respondText { "Failed $errors" }
}

private suspend fun ApplicationCall.loggedInSuccessResponse(callback: Principal) {
    respondText { "Success" }
}
