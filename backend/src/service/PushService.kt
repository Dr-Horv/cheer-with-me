package dev.fredag.cheerwithme.service

import dev.fredag.cheerwithme.model.NotFoundException
import dev.fredag.cheerwithme.model.UserPushArn
import dev.fredag.cheerwithme.model.UserPushArns
import dev.fredag.cheerwithme.model.Users
import dev.fredag.cheerwithme.web.DeviceRegistration
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class PushService(
    private val snsService: SnsService,
    private val userService: UserService) {

    suspend fun registerDeviceToken(nick: String, registration: DeviceRegistration) {
        val user = userService.findUserByNick(nick)
        user ?: throw NotFoundException("No user with nick $nick")

        val arn = snsService.registerWithSNS(registration, lookupArn(user.id))
        Database.dbQuery {
            UserPushArns.insert {
                it[UserPushArns.arn] = arn
                it[userId] = user.id
            } get UserPushArns.id
        }
    }

    suspend fun push(nick: String, message: String) {
        val user = userService.findUserByNick(nick)
        user ?: throw NotFoundException("No user with nick $nick")

        val arn = lookupArn(user.id)
        arn ?: throw NotFoundException("No arn for user")

        snsService.sendPush(arn, message)
    }

    private suspend fun lookupArn(userId: Long): String? = Database.dbQuery {
        UserPushArns.select { UserPushArns.userId.eq(userId) }.limit(1)
            .map(this::toUserPushArn)
            .firstOrNull()
            ?.let(UserPushArn::arn)
    }

    private fun toUserPushArn(row: ResultRow): UserPushArn = UserPushArn(
        id = row[UserPushArns.id],
        userId = row[UserPushArns.userId],
        arn = row[UserPushArns.arn]
    )



}