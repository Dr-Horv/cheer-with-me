package dev.fredag.cheerwithme.data

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.fredag.cheerwithme.data.backend.BackendModule
import dev.fredag.cheerwithme.data.backend.BackendService
import dev.fredag.cheerwithme.data.backend.GoogleUserSignInRequest
import javax.inject.Inject

class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val backendService: BackendService
) {

    suspend fun loginWithGoogle(serverAuthCode: String, token: String) {
        BackendModule.setAccessKey(context, token)
        val resp = backendService.loginGoogle(GoogleUserSignInRequest(serverAuthCode))
        BackendModule.setAccessKey(context, resp.accessToken)
        Log.d("UserRepository", "Access token ${resp.accessToken} set")
    }
}