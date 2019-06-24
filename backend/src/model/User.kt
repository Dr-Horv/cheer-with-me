package dev.fredag.invitation

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 255)
}

data class User(
    val id : Int,
    val name : String
)