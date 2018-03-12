package com.zwolsman.blackjack.discordbot.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.IChannel


object Games : IntIdTable() {
    val seed = long("seed")
    val history = text("history").default("")
    val status = integer("status").default(0)
    val channelId = long("channel_id")
}

class Game(id: EntityID<Int>) : IntEntity(id) {
    fun addUser(user: User, buyIn: Int) {
        transaction {
            user.guildPoints -= buyIn
            GamesUser.new {
                this.user = user
                this.game = this@Game
                this.buyIn = buyIn
            }
        }
    }

    companion object : IntEntityClass<Game>(Games) {
        fun isOpenIn(channelId: Long) = transaction { !Game.find { (Games.channelId eq channelId) and ((Games.status) eq 0 or (Games.status eq 1)) }.empty() }
        fun findInChannel(channelId: Long) = transaction { Game.find { (Games.status neq 2) and (Games.channelId eq channelId) }.firstOrNull() }
        fun findInChannel(channel: IChannel) = findInChannel(channel.longID)
    }

    val users by GamesUser referrersOn GamesUsers.user
    var seed by Games.seed
    var history by Games.history
    var status by Games.status
    var channelId by Games.channelId
}