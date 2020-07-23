package dev.fredag.cheerwithme.service

import dev.fredag.cheerwithme.logger
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
    val log by logger()


    suspend fun registerDeviceToken(userId: Long, registration: DeviceRegistration) {
        val arn = snsService.registerWithSNS(registration, lookupArn(userId))
        Database.dbQuery {
            UserPushArns.insert {
                it[UserPushArns.arn] = arn
                it[UserPushArns.userId] = userId
            } get UserPushArns.id
        }
    }


    suspend fun push(userId: Long, message: String) {
        val user = userService.findUserById(userId)
        if(user == null) {
            log.warn("Missing user when trying to send push userId=$userId")
            return
        }

        val arn = lookupArn(user.id)
        if(arn == null) {
            log.debug("No pushARN for user")
            return
        }

        snsService.sendPush(arn, message)
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