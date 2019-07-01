package dev.fredag.cheerwithme.service

import dev.fredag.cheerwithme.model.User
import dev.fredag.cheerwithme.model.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class UserService {

    suspend fun addUser(newNick: String): User {
        var newId = 0L
        Database.dbQuery {
            newId = Users.insert {
                it[nick] = newNick
            } get Users.id
        }
        return User(newId, newNick)
    }

    suspend fun getUsers(): List<User> = Database.dbQuery {
        Users.selectAll().map(this::toUser)
    }

    suspend fun findUserByNick(nick: String): User? = Database.dbQuery {
        Users.select { Users.nick.eq(nick) }.limit(1).map(this::toUser).firstOrNull()
    }

    private fun toUser(row: ResultRow): User = User(
            id = row[Users.id],
            nick = row[Users.nick]
        )

}