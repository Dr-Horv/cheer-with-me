package dev.fredag.cheerwithme.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant

typealias UserId = Long

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
sealed class Event(val userId: UserId, val timestamp: Instant)
class FriendRequest(userId: UserId, timestamp: Instant, val requester: UserId, val receiver: UserId): Event(userId, timestamp)
class FriendRequestAccepted(userId: UserId, timestamp: Instant, val requester: UserId, val receiver: UserId): Event(userId, timestamp)

class SendFriendRequest(val userId: UserId)
class AcceptFriendRequest(val userId: UserId)

class UserFriendsAggregate(val friends: List<UserId>,
                           val incomingFriendRequests: List<UserId>,
                           val outgoingFriendRequests: List<UserId>)

class UserFriends(val friends: List<User>,
                  val incomingFriendRequests: List<User>,
                  val outgoingFriendRequests: List<User>
)


/*

Use case 1
SendFriendRequest(Horv=>Ndushi)
AcceptFriendRequest(Ndushi=>Horv)

FriendRequestSent(from: UserId, to: UserId)

Horv
FR-sent
FR-sent
FR-received
FR-accepted

FriendRequestAccepted(

 */

