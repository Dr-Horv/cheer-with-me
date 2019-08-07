package dev.fredag.cheerwithme

import com.auth0.jwk.JwkProviderBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import dev.fredag.cheerwithme.service.Database
import dev.fredag.cheerwithme.service.initAwsSdkClients
import io.ktor.application.*
import dev.fredag.cheerwithme.service.*
import dev.fredag.cheerwithme.web.friendRouting
import dev.fredag.cheerwithme.web.pushRouting
import dev.fredag.cheerwithme.web.userRouting
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.jackson
import io.ktor.request.host
import io.ktor.request.path
import io.ktor.request.port
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import org.slf4j.event.Level
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import java.net.URL
import java.net.http.HttpResponse
import java.util.concurrent.TimeUnit


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class AppleOauthResponse(
    val access_token: String,
    val expires_in: Long,
    val id_token: String,
    val refresh_token: String,
    val token_type: String)
//Dependency injection without magic? Instantiate service classes for insertion in "modules" (routes) here
val userService : UserService = UserService()
val snsService : SnsService = SnsService(buildSnsClient())
val pushService : PushService = PushService(snsService, userService)

data class AppleUserSignInRequest(val code: String)

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

        jwt("apple") {
            verifier(
                JwkProviderBuilder(URL("https://appleid.apple.com/auth/keys"))
                .cached(10, 24, TimeUnit.HOURS)
                .rateLimited(10, 1, TimeUnit.MINUTES)
                .build(),
                "https://appleid.apple.com"
            )
            validate { credentials ->
                log.debug("$credentials")
                log.debug("${credentials.payload}")
                log.debug(ObjectMapper().writeValueAsString(credentials))
                JWTPrincipal(credentials.payload)
            }

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
            log.debug("$body")
            call.respond(body)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        authenticate("apple") {
            post("/login/apple") {
                val appleUserSignInRequest = call.receive<AppleUserSignInRequest>()

                log.debug("Code ${appleUserSignInRequest.code}")

                val data = Parameters.build {
                    append("grant_type", "authorization_code")
                    append("code", appleUserSignInRequest.code)
                    // append("redirect_uri" , $redirect_uri)
                    append("client_id" , "dev.fredag.CheerWithMe")
                    append("client_secret" , "eyJraWQiOiJHNlA5NlIzNzdZIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJSS0hWM1daOUNSIiwiaWF0IjoxNTYyNzg4MjMzLCJleHAiOjE1NzgzNDAyMzMsImF1ZCI6Imh0dHBzOi8vYXBwbGVpZC5hcHBsZS5jb20iLCJzdWIiOiJkZXYuZnJlZGFnLkNoZWVyV2l0aE1lIn0.JS8YcwgGcsEFWDf_dhVwVM5IZf7451_Xp84o7_kaOoSP-z6n1zlXQJQMZXFmxG5adcjStxLeSuSnHOLCgPz-ig")
                }

                log.debug("Sending form with data: $data")
                val body = HttpClient().submitForm<String>(
                    url = "https://appleid.apple.com/auth/token",
                    formParameters = data,
                    encodeInQuery = false,
                    block = { header("user-agent", "cheer-with-me") }
                )

                /**
                 * Can't parse atm
                 */
                val appleOauthResponse = ObjectMapper().readValue<AppleOauthResponse>(body)

                log.debug("RESPONSE $appleOauthResponse")
                call.respond(mapOf("accessToken" to appleOauthResponse.access_token))

                // TODO Store access token for user lookup
                // Create and store user (use sub
                // Implement custom auth lookup on access token
                /*
                val principal = call.authentication.principal<JWTPrincipal>()!!
                principal.payload.subject

                 */


                /*
                if(httpResponse.statusCode() == 200) {
                    val body = httpResponse.body()
                    log.debug("Success! $body")
                    call.respond(mapOf("accessToken" to body))

                    // Store Access token!
                } else {
                    log.debug("Failed! $httpResponse")
                    call.respond(mapOf("error" to "failed"))
                }

                 */
            }
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
                    log.debug(id)
                    log.debug("$data")
                    call.loggedInSuccessResponse(principal)
                }

            }

            //Put all other externally defined routes here (if they require authentication)
            routing {
                userRouting(userService)
                pushRouting(pushService)
                friendRouting()
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

private fun buildSnsClient(): SnsClient {
    return SnsClient
        .builder()
        .region(Region.EU_CENTRAL_1)
        .build()
}
