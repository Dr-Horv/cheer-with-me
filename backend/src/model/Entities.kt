package dev.fredag.cheerwithme.model

import dev.fredag.cheerwithme.model.UserFriendsEvents.autoIncrement
import dev.fredag.cheerwithme.model.UserFriendsEvents.primaryKey
import dev.fredag.cheerwithme.model.UserFriendsEvents.references
import dev.fredag.cheerwithme.model.UserPushArns.userId
import dev.fredag.cheerwithme.model.Users.autoIncrement
import dev.fredag.cheerwithme.model.Users.primaryKey
import dev.fredag.cheerwithme.objectMapper
import dev.fredag.cheerwithme.service.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchInsert
import org.joda.time.DateTime
import java.time.Instant

object Users : Table() {
    val id = long("id").primaryKey().autoIncrement()
    val googleId = varchar("google_id", 255).nullable()
    val appleId = varchar("apple_id", 255).nullable()
    val accessToken = varchar("access_token", 255).nullable()
    val refreshToken = varchar("refresh_token", 255).nullable()
    val nick = varchar("nick", 255)
    val avatarUrl = varchar("avatar_url", 1024).nullable()
}

data class User(
    val id : Long,
    val nick : String,
    val avatarUrl: String? = null
)

data class UserWithToken(val id: Long, val accessToken: String)

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

object UserFriendsEvents: Table() {
    val id = long("id").primaryKey().autoIncrement()
    val timestamp = datetime("timestamp")
    val userId = (long("user_id") references Users.id)
    val eventData = text("event_data")
}

object HappeningEvents: Table() {
    val id = long("id").primaryKey().autoIncrement()
    val timestamp = datetime("timestamp")
    val userId = (long("user_id") references Users.id)
    val happeningId = varchar("happening_id", 256)
    val eventData = text("event_data")
}

