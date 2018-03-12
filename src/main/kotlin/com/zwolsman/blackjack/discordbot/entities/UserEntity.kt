package com.zwolsman.blackjack.discordbot.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and

object Users : IntIdTable() {
    val name = text("name")
    val discordId = long("discord_user_id")
    val guildId = long("guild_id")
    val guildPoints = long("guild_points")
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users) {
        fun findInGuildAndChannel(guildId: Long, discordId: Long) = User.find { (Users.discordId eq discordId) and (Users.guildId eq guildId) }
    }

    var name by Users.name
    var discordId by Users.discordId
    val mention: String
        get() = "<@!$discordId>"
    var guildId by Users.guildId
    var guildPoints by Users.guildPoints
    val games by GamesUser referrersOn GamesUsers.user
}