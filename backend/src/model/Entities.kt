package dev.fredag.cheerwithme.model

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = long("id").primaryKey().autoIncrement()
    val googleId = varchar("google_id", 255).nullable()
    val appleId = varchar("apple_id", 255).nullable()
    val nick = varchar("nick", 255)
}

data class User(
    val id : Long,
    val nick : String
)

object UserPushArns : Table() {
    val id = long("id").primaryKey().autoIncrement()
    val userId = (long("user_id") references Users.id)
    val arn = varchar("arn", 255)
}

data class UserPushArn(
    val id: Long,
    val userId: Long,
    val arn: String
)

