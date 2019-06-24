package dev.fredag.web

import dev.fredag.service.UserService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

//This endpoint is probably not useful later on - the endpoint will probably be rolled into a login/signup.
//Use for development purposes and whatnot.

data class AddUserRequest(val user : String)

fun Application.userModule(testing: Boolean = false){

    val userService = UserService()

    routing {
        post("/user/"){
            val newUserName = call.receive<AddUserRequest>()
            val newUser = userService.addUser(newUserName.user)
            call.respond(newUser)
        }

        routing {
            get("/user/"){
                call.respond(userService.getUsers())
            }
        }
    }


}