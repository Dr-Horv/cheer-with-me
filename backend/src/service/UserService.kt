package dev.fredag.service

import dev.fredag.invitation.DatabaseFactory
import dev.fredag.invitation.User
import dev.fredag.invitation.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class UserService {
    suspend fun addUser(newName : String) : User {
        var newId = 0
        DatabaseFactory.dbQuery {
            newId = Users.insert {
                it[name] = newName
            } get Users.id
        }
        return User(newId, newName)
    }
    suspend fun getUsers(): List<User> = DatabaseFactory.dbQuery {
        Users.selectAll().map { toUser(it) }
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[Users.id],
            name = row[Users.name]
        )
}