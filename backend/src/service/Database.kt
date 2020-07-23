package dev.fredag.cheerwithme.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.fredag.cheerwithme.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.transactions.transaction


object Database {
    fun init() {
        val userService = UserService()
        val userFriendsService = UserFriendsService(userService)

        // Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        Database.connect(hikari())
        transaction {
            create(Users, UserPushArns, UserFriendsEvents)
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
