package dev.fredag.cheerwithme.friends

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

data class FriendRequest(val adder:String, val adee:String)

fun Application.friendModule(testing: Boolean = false){
    routing {
        get("/friends/"){
            call.respondText("You have no friends :,(",
                contentType = ContentType.Text.Plain,
                status = HttpStatusCode.OK)
        }

        post("/friends/add"){
            val friendReq = call.receive<FriendRequest>()
            val sb = StringBuilder()
            sb.append(friendReq.adder)
                .append(" added ")
                .append(friendReq.adee)

            call.respondText( sb.toString(), contentType = ContentType.Text.Plain)
        }
    }
}