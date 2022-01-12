package dev.fredag.cheerwithme.data.backend

import java.time.Instant

data class GoogleUserSignInRequest(val code: String)
data class GoogleUserSignInResponse(val accessToken: String)
typealias UserId = Long

data class User(
    val id : UserId,
    val nick : String,
    val avatarUrl: String? = null
)

data class UserFriends(val friends: List<User> = emptyList(),
                       val incomingFriendRequests: List<User> = emptyList(),
                       val outgoingFriendRequests: List<User> = emptyList())

class AcceptFriendRequest(val userId: UserId)
class SendFriendRequest(val userId: UserId)

data class Happening(
    val happeningId: HappeningId,
    val admin: User,
    val name: String,
    val description: String,
    val time: Instant,
    val location: Location?,
    val attendees: List<User>,
    val awaiting: List<User>,
    val cancelled: Boolean
)
typealias HappeningId = String
class Coordinate(val lat: Double, val lng: Double)
class Location(val coordinate: Coordinate)

class CreateHappening(
    val name: String,
    val description: String,
    val time: Instant,
    val location: Location?,
    val usersToInvite: List<UserId>
)

class UpdateHappening(
    val happeningId: HappeningId,
    val name: String?,
    val description: String?,
    val time: Instant?,
    val location: Location?
)

class CancelHappening(val happeningId: HappeningId, val reason: String?)
class InviteUsersToHappening(val happeningId: HappeningId, val usersToInvite: List<UserId>)
class AcceptHappeningInvite(val happeningId: HappeningId)
class RejectHappeningInvite(val happeningId: HappeningId)
