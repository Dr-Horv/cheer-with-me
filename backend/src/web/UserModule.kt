package dev.fredag.cheerwithme.web

import dev.fredag.cheerwithme.getUserId
import dev.fredag.cheerwithme.service.SearchService
import dev.fredag.cheerwithme.service.UserService
import io.ktor.application.call
import io.ktor.http.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post


fun Route.userRouting(userService: UserService, searchService: SearchService, testing: Boolean = false){

    get("/user/me/") {
        val user = userService.findUserById(getUserId());
        if(user == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        call.respond(HttpStatusCode.OK, user)
    }

    //This endpoint is probably not useful later on - the endpoint will probably be rolled into a login/signup.
    //Use for development purposes and whatnot.
    get("/users/"){
        call.respond(userService.getUsers())
    }

    get("/users/search/") {
        val nick = call.request.queryParameters["nick"]
        val users = if (nick != null) { searchService.searchUserByNick(nick, getUserId()) } else { emptyList() }
        call.respond(users)
    }
}



