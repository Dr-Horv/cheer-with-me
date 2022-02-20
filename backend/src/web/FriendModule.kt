package dev.fredag.cheerwithme.web

import dev.fredag.cheerwithme.getUserId
import dev.fredag.cheerwithme.model.AcceptFriendRequest
import dev.fredag.cheerwithme.model.SendFriendRequest
import dev.fredag.cheerwithme.service.UserFriendsService
import dev.fredag.cheerwithme.service.UserService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post


fun Route.friendRouting(
    userFriendsService: UserFriendsService, testing: Boolean = false) {
    get("/friends") {
        call.respond(
            userFriendsService.getUserFriends(getUserId())
        )
    }

    post("/friends/sendFriendRequest") {
        val friendReq = call.receive<SendFriendRequest>()
        userFriendsService.sendFriendRequest(getUserId(), friendReq)
        call.respond(HttpStatusCode.NoContent)
    }

    post("/friends/acceptFriendRequest") {
        val acceptReq = call.receive<AcceptFriendRequest>()
        userFriendsService.acceptFriendRequest(getUserId(), acceptReq)
        call.respond(HttpStatusCode.NoContent)
    }
}

