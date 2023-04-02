package dev.fredag.cheerwithme.web

import dev.fredag.cheerwithme.getUserId
import dev.fredag.cheerwithme.model.*
import dev.fredag.cheerwithme.service.HappeningService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.happeningRouting(happeningService: HappeningService) {

    get("/happenings") {
        val happenings= happeningService.getHappenings(getUserId())
        call.respond(HttpStatusCode.OK, happenings)
    }


    get("/happenings/{happeningId}") {
        val happeningId = call.parameters["happeningId"]
        if(happeningId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing happeningId from path")
            return@get
        }
        val happening = happeningService.getHappening(happeningId)
        if(happening == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        call.respond(HttpStatusCode.OK, happening)
    }

    post("/happenings/createHappening") {
        val createHappeningRequest = call.receive<CreateHappening>()
        val happening = happeningService.createHappening(getUserId(), createHappeningRequest)
        call.respond(HttpStatusCode.Created, happening)
    }

    put("/happenings/updateHappening") {
        val updateHappeningRequest = call.receive<UpdateHappening>()
        val happening = happeningService.updateHappening(getUserId(), updateHappeningRequest)
        if(happening == null) {
            call.respond(HttpStatusCode.NotFound)
            return@put
        }
        call.respond(HttpStatusCode.Accepted, happening)
    }

    delete("/happenings/cancelHappening") {
        val cancelHappeningRequest = call.receive<CancelHappening>()
        happeningService.cancelHappening(getUserId(), cancelHappeningRequest)
        call.respond(HttpStatusCode.NoContent)
    }

    put("/happenings/inviteUsers") {
        val inviteUsersRequest = call.receive<InviteUsersToHappening>()
        val happening = happeningService.inviteUsers(getUserId(), inviteUsersRequest)
        if(happening == null) {
            call.respond(HttpStatusCode.NotFound)
            return@put
        }
        call.respond(HttpStatusCode.Accepted, happening)
    }

    post("/happenings/acceptHappeningInvite") {
        val acceptHappeningInviteRequest = call.receive<AcceptHappeningInvite>()
        val happening = happeningService.acceptHappeningInvite(getUserId(), acceptHappeningInviteRequest)
        if(happening == null) {
            call.respond(HttpStatusCode.NotFound)
            return@post
        }
        call.respond(HttpStatusCode.Accepted, happening)
    }

    post("/happenings/declineHappeningInvite") {
        val declineHappeningInviteRequest = call.receive<DeclineHappeningInvite>()
        val happening = happeningService.declineHappeningInvite(getUserId(), declineHappeningInviteRequest)
        if(happening == null) {
            call.respond(HttpStatusCode.NotFound)
            return@post
        }
        call.respond(HttpStatusCode.Accepted, happening)
    }




}