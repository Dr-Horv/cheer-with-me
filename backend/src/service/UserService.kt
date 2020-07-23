package dev.fredag.cheerwithme.service

import dev.fredag.cheerwithme.model.User
import dev.fredag.cheerwithme.model.UserWithToken
import dev.fredag.cheerwithme.model.Users
import dev.fredag.cheerwithme.userService
import org.jetbrains.exposed.sql.*

class UserService {
    suspend fun addUser(newNick: String,
                        accessToken: String,
                        googleId: String? = null,
                        appleId: String? = null,
                        refreshToken: String?): User {
        var newId = 0L
        Database.dbQuery {
            newId = Users.insert {
                it[nick] = newNick
                it[Users.accessToken] = accessToken
                googleId?.apply { it[Users.googleId] = googleId }
                appleId?.apply { it[Users.appleId] = appleId }
                refreshToken?.apply { it[Users.refreshToken] = refreshToken }
            } get Users.id
        }
        return User(newId, newNick)
    }

    suspend fun getUsers(): List<User> = Database.dbQuery {
        Users.selectAll().map(this::toUser)
    }

    suspend fun upsertUserWithId(nick: String,
                                 accessToken: String,
                                 refreshToken: String? = null,
                                 googleId: String? = null,
                                 appleId: String? = null
                                 ) {
        val user = findUserByGoogleOrAppleId(googleId ?: appleId ?: "")
        if (user == null) {
            userService.addUser(
                newNick = nick,
                accessToken = accessToken,
                googleId = googleId,
                appleId = appleId,
                refreshToken = refreshToken
            )
        } else {
            Database.dbQuery {
                Users.update ({ Users.id.eq(user.id) }) {
                    it[Users.accessToken] = accessToken
                }
            }
        }
    }

    suspend fun findUserByGoogleOrAppleId(id: String): User? = Database.dbQuery {
        Users.select { Users.googleId.eq(id) or Users.appleId.eq(id) }.limit(1).map(this::toUser).firstOrNull()
    }

    suspend fun findUserByNick(nick: String): User? = Database.dbQuery {
        Users.select { Users.nick.eq(nick) }.limit(1).map(this::toUser).firstOrNull()
    }

    suspend fun findUserByAccessToken(accessToken: String): UserWithToken? = Database.dbQuery {
        Users.select { Users.accessToken.eq(accessToken) }.map(this::toUserWithToken).firstOrNull()
    }

    suspend fun findUsersWithAccessToken(): List<UserWithToken> = Database.dbQuery {
        Users.select { Users.accessToken.isNotNull()}.map(this::toUserWithToken).toList()
    }

    private fun toUserWithToken(row: ResultRow): UserWithToken = UserWithToken(
        id = row[Users.id],
        accessToken = row[Users.accessToken].orEmpty()
    )

    private fun toUser(row: ResultRow): User = User(
            id = row[Users.id],
            nick = row[Users.nick]
        )
}