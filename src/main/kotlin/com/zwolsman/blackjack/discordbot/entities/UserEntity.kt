package com.zwolsman.blackjack.discordbot.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object Users : IntIdTable() {
    val name = text("name")
    val discordId = long("discord_user_id")
    val guildId = long("guild_id")
    val guildPoints = long("guild_points")
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users) {
        fun findInGuildAndChannel(guildId: Long, discordId: Long) = User.find { (Users.discordId eq discordId) and (Users.guildId eq guildId) }
        fun findByName(name: String, guildId: Long): User? = transaction { User.find { (Users.name eq name) and (Users.guildId eq guildId) }.firstOrNull() }
        fun findByDiscordId(discordId: Long, guildId: Long) = transaction { User.find { (Users.discordId eq discordId) and (Users.guildId eq guildId) }.firstOrNull() }
    }

    var name by Users.name
    var discordId by Users.discordId
    val mention: String
        get() = "<@!$discordId>"
    var guildId by Users.guildId
    var guildPoints by Users.guildPoints
    val games by GamesUser referrersOn GamesUsers.user
}