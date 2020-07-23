package dev.fredag.cheerwithme.web

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post


data class FriendRequest(val adder: String, val adee: String)

fun Route.friendRouting(testing: Boolean = false) {
    get("/friends/") {
        call.respondText(
            "You have no friends :,(",
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.OK
        )
    }

    post("/friends/add/") {
        val friendReq = call.receive<FriendRequest>()
        val sb = StringBuilder()
        sb.append(friendReq.adder)
            .append(" added ")
            .append(friendReq.adee)

        call.respondText(sb.toString(), contentType = ContentType.Text.Plain)
    }
}