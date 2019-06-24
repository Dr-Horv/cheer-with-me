package dev.fredag.cheerwithme.invitation

import io.ktor.application.Application
import io.ktor.routing.post
import io.ktor.routing.routing

data class DrinkingInvitation(val drinkType : String, val message : String, val coordinates : String)


fun Application.invitationModule(testing: Boolean = false){
    //Todo: JWT-authing

    routing {
        post("/invite/"){
            //val authHeader = call.request.parseAuthorizationHeader()
            //TODO: Fetch your friendlist
            //Todo: notify all of em
        }
    }
}