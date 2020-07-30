package dev.fredag.cheerwithme.data.backend

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