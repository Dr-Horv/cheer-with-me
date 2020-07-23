package dev.fredag.cheerwithme.service

import dev.fredag.cheerwithme.web.CheerWithMePrincipal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class AuthService(val userService: UserService) {
    private val cache: MutableMap<String, Long> = ConcurrentHashMap()

    fun init() {
        GlobalScope.launch {
            userService.findUsersWithAccessToken()
                .forEach { user -> cache[user.accessToken] = user.id }
        }
    }

    suspend fun verifyToken(token: String): CheerWithMePrincipal? {
        var userId = cache[token]
        if(userId == null) {
           val user = userService.findUserByAccessToken(token)
            if(user == null) {
                return null
            } else {
                cache[token] = user.id
                userId = user.id
            }
        }
        return CheerWithMePrincipal(userId = userId)
    }
}