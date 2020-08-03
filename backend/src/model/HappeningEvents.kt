package dev.fredag.cheerwithme.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
sealed class HappeningEvent(val userId: UserId, val happeningId: HappeningId, val timestamp: Instant)
class HappeningCreated(
    timestamp: Instant,
    userId: UserId,
    happeningId: HappeningId,
    val name: String,
    val description: String,
    val time: Instant,
    val location: Location?
) : HappeningEvent(userId, happeningId, timestamp)

class HappeningNameChanged(userId: UserId, timestamp: Instant, happeningId: HappeningId, val name: String) :
    HappeningEvent(userId, happeningId, timestamp)

class HappeningDescriptionChanged(
    userId: UserId,
    timestamp: Instant,
    happeningId: HappeningId,
    val description: String
) :
    HappeningEvent(userId, happeningId, timestamp)


class HappeningTimeChanged(userId: UserId, timestamp: Instant, happeningId: HappeningId, val time: Instant) :
    HappeningEvent(userId, happeningId, timestamp)

class HappeningLocationChanged(userId: UserId, timestamp: Instant, happeningId: HappeningId, val location: Location) :
    HappeningEvent(userId, happeningId, timestamp)

class HappeningCancelled(userId: UserId, timestamp: Instant, happeningId: HappeningId, reason: String? = "") :
    HappeningEvent(userId, happeningId, timestamp)

class UserInvitedToHappening(
    userId: UserId,
    timestamp: Instant,
    happeningId: HappeningId,
    val invited: UserId
) : HappeningEvent(userId, happeningId, timestamp)

class UserAcceptedHappeningInvite(
    userId: UserId,
    timestamp: Instant,
    happeningId: HappeningId
) : HappeningEvent(userId, happeningId, timestamp)

class UserRejectedHappeningInvite(
    userId: UserId,
    timestamp: Instant,
    happeningId: HappeningId
) : HappeningEvent(userId, happeningId, timestamp)

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
