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
            drop(HappeningEvents)
            create(Users, UserPushArns, UserFriendsEvents, HappeningEvents)
            GlobalScope.launch {
                userService.upsertUserWithId(
                    "Horv",
                    "horvtoken",
                    googleId = "104624699130264441197"
                )
                userService.upsertUserWithId(
                    "Ndushi",
                    "ndushitoken",
                    appleId = "Ndushi",
                    googleId = "110573574311424580117"
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
                    googleId = "113108127712275255357"
                )
                userService.upsertUserWithId(
                    "Meddan",
                    "Meddan",
                    appleId = "Meddan"
                )
                userService.upsertUserWithId(
                    "Malm",
                    "Malm",
                    appleId = "Malm"
                )

                val HORV_ID = 1L
                val NDUSHI_ID = 2L
                val KALIOR_ID = 3L
                val JUICE_ID = 4L
                val TEJP_ID = 5L
                val MEDDAN_ID = 6L
                val MALM_ID = 7L

                userFriendsService.sendFriendRequest(
                    HORV_ID,
                    SendFriendRequest(NDUSHI_ID)
                )
                userFriendsService.sendFriendRequest(
                    NDUSHI_ID,
                    SendFriendRequest(HORV_ID)
                )
                userFriendsService.acceptFriendRequest(HORV_ID, AcceptFriendRequest(NDUSHI_ID))
                userFriendsService.sendFriendRequest(HORV_ID, SendFriendRequest(JUICE_ID))
                userFriendsService.sendFriendRequest(HORV_ID, SendFriendRequest(TEJP_ID))
                userFriendsService.acceptFriendRequest(TEJP_ID, AcceptFriendRequest(HORV_ID))

                userFriendsService.sendFriendRequest(MEDDAN_ID, SendFriendRequest(HORV_ID))
                userFriendsService.acceptFriendRequest(HORV_ID, AcceptFriendRequest(MEDDAN_ID))

                userFriendsService.sendFriendRequest(MEDDAN_ID, SendFriendRequest(MALM_ID))
                userFriendsService.acceptFriendRequest(MALM_ID, AcceptFriendRequest(MEDDAN_ID))

                userFriendsService.sendFriendRequest(MEDDAN_ID, SendFriendRequest(KALIOR_ID))
                userFriendsService.acceptFriendRequest(KALIOR_ID, AcceptFriendRequest(MEDDAN_ID))

                userFriendsService.sendFriendRequest(TEJP_ID, SendFriendRequest(MEDDAN_ID))
                userFriendsService.sendFriendRequest(JUICE_ID, SendFriendRequest(TEJP_ID))


                happeningService.createHappening(HORV_ID, CreateHappening("Discode!", "Kod & Vin",
                    Instant.now().plus(1, ChronoUnit.DAYS), null, listOf(MALM_ID, NDUSHI_ID)))

                val happening = happeningService.createHappening(TEJP_ID, CreateHappening("Happidyhaps!", ":D",
                    Instant.now().plus(1, ChronoUnit.DAYS), Location(Coordinate(58.298584, 12.961619)), listOf(HORV_ID, NDUSHI_ID)))
                happeningService.acceptHappeningInvite(HORV_ID, AcceptHappeningInvite(happening.happeningId))

            }
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:~/test;AUTO_SERVER=TRUE" // "jdbc:h2:mem:test"
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
