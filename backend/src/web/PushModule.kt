package dev.fredag.cheerwithme.web

import dev.fredag.cheerwithme.model.Platform
import dev.fredag.cheerwithme.service.PushService
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post

data class DeviceRegistration(val pushToken: String, val platform: Platform)

fun Route.pushRouting(pushService: PushService, testing: Boolean = false) {
    post("/push/register-device") {
        val registration = call.receive<DeviceRegistration>()
        val userId = call.principal<CheerWithMePrincipal>()!!.userId
        pushService.registerDeviceToken(userId, registration)
        call.respond(HttpStatusCode.NoContent)
    }

    post("/push/test/{nick}") {
        pushService.push(call.parameters["nick"]!!, call.receive())
        call.respond(HttpStatusCode.OK)
    }
}
