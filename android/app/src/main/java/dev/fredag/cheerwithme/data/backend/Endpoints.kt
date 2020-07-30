package dev.fredag.cheerwithme.data.backend

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class GoogleUserSignInRequest(val code: String)
data class GoogleUserSignInResponse(val accessToken: String)

data class User(
    val id : Long,
    val nick : String,
    val avatarUrl: String? = null
)

data class UserFriends(val friends: List<User>,
                  val incomingFriendRequests: List<User>,
                  val outgoingFriendRequests: List<User>)


interface BackendService {
    @POST("/login/google")
    suspend fun loginGoogle(@Body googleUserSignInRequest: GoogleUserSignInRequest): GoogleUserSignInResponse

    @GET("/friends/")
    suspend fun friends(): UserFriends
}