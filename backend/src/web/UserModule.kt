package dev.fredag.cheerwithme.web

import dev.fredag.cheerwithme.service.UserService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post

//This endpoint is probably not useful later on - the endpoint will probably be rolled into a login/signup.
//Use for development purposes and whatnot.

fun Route.userRouting(userService: UserService, testing: Boolean = false){

    get("/users/"){
        call.respond(userService.getUsers())
    }

    get("/users/search/") {
        val nick = call.request.queryParameters["nick"]
        val users = if (nick != null) { userService.searchUserByNick(nick) } else { emptyList() }
        call.respond(users)
    }
}



