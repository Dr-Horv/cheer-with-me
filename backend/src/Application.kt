package dev.fredag.cheerwithme

import com.auth0.jwk.JwkProviderBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dev.fredag.cheerwithme.model.AppleOauthResponse
import dev.fredag.cheerwithme.model.AppleUserSignInRequest
import dev.fredag.cheerwithme.model.GoogleOauthResponse
import dev.fredag.cheerwithme.model.GoogleUserSignInRequest
import dev.fredag.cheerwithme.service.*
import dev.fredag.cheerwithme.web.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.Dispatchers
import org.slf4j.event.Level
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import java.net.URL
import java.util.concurrent.TimeUnit


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val objectMapper: ObjectMapper = ObjectMapper()
    .registerKotlinModule()
    .registerModule(JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)


//Dependency injection without magic? Instantiate service classes for insertion in "modules" (routes) here
private val userService: UserService = UserService()
private val snsService: SnsService = SnsService(buildSnsClient())
private val pushService: PushService = PushService(snsService, userService)
private val oauth2Service: Oauth2Service = Oauth2Service()
private val authService: AuthService = AuthService(userService)
private val userFriendsService: UserFriendsService = UserFriendsService(userService, pushService)

@KtorExperimentalAPI
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

    install(Authentication) {

        jwt("google") {
            verifier(
                JwkProviderBuilder(URL("https://www.googleapis.com/oauth2/v3/certs"))
                    .cached(10, 24, TimeUnit.HOURS)
                    .rateLimited(10, 1, TimeUnit.MINUTES)
                    .build(),
                "https://accounts.google.com"
            )
            validate { credentials ->
                log.debug("$credentials")
                log.debug("${credentials.payload}")
                log.debug(objectMapper.writeValueAsString(credentials))
                JWTPrincipal(credentials.payload)
            }
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
                log.debug(objectMapper.writeValueAsString(credentials))
                JWTPrincipal(credentials.payload)
            }

        }

        cheerWithMe("cheerWithMe") {
            verifyToken {
                when {
                    it.isNotBlank() -> authService.verifyToken(it)
                    else -> null
                }
            }
        }
    }

    Database.init()
    authService.init()
    snsService.init(
        pushArnIOS = environment.config.property("push.pushArnIOS").getString(),
        pushArnAndroid = environment.config.property("push.pushArnAndroid").getString()
    )
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

        authenticate("apple") {
            post("/login/apple") {
                val appleUserSignInRequest = call.receive<AppleUserSignInRequest>()
                call.application.log.debug("Code ${appleUserSignInRequest.code}")
                val appleOauthResponse = oauth2Service.authenticate<AppleOauthResponse>(
                    "https://appleid.apple.com/auth/token", Oauth2Parameters(
                        grantType = "authorization_code",
                        code = appleUserSignInRequest.code,
                        clientId = application.environment.config.property("oauth.apple.client_id").getString(),
                        clientSecret = application.environment.config.property("oauth.apple.client_secret").getString(),
                        redirectUri = null
                    )
                )

                val principal = call.authentication.principal<JWTPrincipal>()!!
                userService.upsertUserWithId(
                    appleId = principal.payload.subject,
                    nick = appleUserSignInRequest.nick,
                    accessToken = appleOauthResponse.access_token,
                    refreshToken = appleOauthResponse.refresh_token
                )

                call.application.log.debug("RESPONSE $appleOauthResponse")
                call.respond(mapOf("accessToken" to appleOauthResponse.access_token))
            }
        }

        authenticate("google") {
            post("/login/google") {
                val googleUserSignInRequest = call.receive<GoogleUserSignInRequest>()
                log.debug("Code ${googleUserSignInRequest.code}")

                val googleOauthResponse = oauth2Service.authenticate<GoogleOauthResponse>(
                    "https://oauth2.googleapis.com/token",
                    Oauth2Parameters(
                        grantType = "authorization_code",
                        code = googleUserSignInRequest.code,
                        redirectUri = "",
                        clientId = application.environment.config.property("oauth.google.client_id").getString(),
                        clientSecret = application.environment.config.property("oauth.google.client_secret").getString()
                    )
                )


                val json = HttpClient().get<String>("https://www.googleapis.com/userinfo/v2/me") {
                    header("Authorization", "Bearer ${googleOauthResponse.access_token}")
                }

                val googleUser = objectMapper.readValue<Map<String, String>>(json)
                val id = googleUser.getValue("id")
                log.debug(json);
                log.debug(googleUser.toString())
                userService.upsertUserWithId(
                    googleId = id,
                    nick = googleUser.getValue("name"),
                    accessToken = googleOauthResponse.access_token
                )

                call.respond(mapOf("accessToken" to googleOauthResponse.access_token))
            }
        }

        authenticate("cheerWithMe") {
            userRouting(userService)
            pushRouting(pushService)
            friendRouting(userFriendsService)
            get("/safe") {
                call.respond(mapOf(
                    "secret" to "hello cheerWithMe",
                    "user" to call.principal<CheerWithMePrincipal>()!!.userId
                ))
            }
        }

    }
}

fun buildSnsClient(): SnsClient {
    return SnsClient
        .builder()
        .region(Region.EU_CENTRAL_1)
        .build()
}

fun PipelineContext<Unit, ApplicationCall>.getUserId() =
    call.principal<CheerWithMePrincipal>()!!.userId