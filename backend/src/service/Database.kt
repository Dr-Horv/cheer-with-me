package dev.fredag.cheerwithme.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.fredag.cheerwithme.buildSnsClient
import dev.fredag.cheerwithme.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.temporal.ChronoUnit

fun now(): Instant = Instant.now()

object Database {
    fun init() {
        val userService = UserService()
        val pushService = PushService(SnsService(buildSnsClient()), userService)
        val userFriendsService = UserFriendsService(userService, pushService = pushService)
        val happeningService = HappeningService(userService = UserService(), pushService = pushService)

        // Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        Database.connect(hikari())
        transaction {
            drop(UserFriendsEvents)
            create(Users, UserPushArns, UserFriendsEvents, HappeningEvents)
            GlobalScope.launch {
                userService.upsertUserWithId(
                    "Horv",
                    "horvtoken",
                    googleId = "Horv"
                )
                userService.upsertUserWithId(
                    "Ndushi",
                    "ndushitoken",
                    appleId = "Ndushi"
                )
                userService.upsertUserWithId(
                    "Kalior",
                    "Kalior",
                    googleId = "Kalior"
                )
                userService.upsertUserWithId(
                    "Juice",
                    "Juice",
                    appleId = "Juice"
                )
                userService.upsertUserWithId(
                    "Tejp",
                    "Tejp",
                    googleId = "Tejp"
                )
                userService.upsertUserWithId(
                    "Meddan",
                    "Meddan",
                    appleId = "Meddan"
                )
                userFriendsService.sendFriendRequest(
                    34,
                    SendFriendRequest(1)
                )
                userFriendsService.sendFriendRequest(
                    38,
                    SendFriendRequest(1)
                )
                userFriendsService.acceptFriendRequest(1, AcceptFriendRequest(38))
                userFriendsService.sendFriendRequest(1, SendFriendRequest(36))
                userFriendsService.sendFriendRequest(1, SendFriendRequest(37))
                userFriendsService.acceptFriendRequest(37, AcceptFriendRequest(1))

                userFriendsService.sendFriendRequest(66, SendFriendRequest(1))
                userFriendsService.acceptFriendRequest(1, AcceptFriendRequest(66))

                userFriendsService.sendFriendRequest(66, SendFriendRequest(38))
                userFriendsService.acceptFriendRequest(38, AcceptFriendRequest(66))

                userFriendsService.sendFriendRequest(66, SendFriendRequest(39))
                userFriendsService.acceptFriendRequest(39, AcceptFriendRequest(66))

                userFriendsService.sendFriendRequest(37, SendFriendRequest(66))


                happeningService.createHappening(66, CreateHappening("Discode!", "Kod & Vin",
                    Instant.now().plus(1, ChronoUnit.DAYS), null, listOf(1, 38)))

                val happening = happeningService.createHappening(38, CreateHappening("Happidyhaps!", ":D",
                    Instant.now().plus(1, ChronoUnit.DAYS), null, listOf(66, 38)))
                happeningService.acceptHappeningInvite(1, AcceptHappeningInvite(happening.happeningId))

            }
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:~/test" // "jdbc:h2:mem:test"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(
        block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }

}
