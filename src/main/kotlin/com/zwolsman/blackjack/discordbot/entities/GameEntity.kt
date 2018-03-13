package com.zwolsman.blackjack.discordbot.entities

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.core.game.Player
import com.zwolsman.blackjack.discordbot.currentHand
import com.zwolsman.blackjack.discordbot.currentPlayer
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.IChannel


object Games : IntIdTable() {
    val seed = long("seed")
    val history = text("history").default("")
    val status = integer("status").default(0)
    val channelId = long("channel_id")
}

typealias GameInstance = com.zwolsman.blackjack.core.Game

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

    fun addMove(option: Option) {
        transaction {
            history = (history.split(",").filter { !it.isBlank() } + listOf(option.ordinal)).joinToString(separator = ",")
        }

    }

    fun isTurnUser(user: User): Boolean {
        return transaction {

            val pairs = this@Game.users.zip(instance.players)
            val currentPlayer = instance.currentPlayer
            pairs.indexOfFirst { it.second == instance.currentPlayer && it.first.user.discordId == user.discordId }
        } != -1
    }

    companion object : IntEntityClass<Game>(Games) {
        fun isOpenIn(channelId: Long) = transaction { !Game.find { (Games.channelId eq channelId) and ((Games.status) eq 0 or (Games.status eq 1)) }.empty() }
        fun findInChannel(channelId: Long) = transaction { Game.find { (Games.status neq 2) and (Games.channelId eq channelId) }.firstOrNull() }
        fun findInChannel(channel: IChannel) = findInChannel(channel.longID)
    }

    val users by GamesUser referrersOn GamesUsers.game
    var seed by Games.seed
    var history by Games.history
    var status by Games.status
    var channelId by Games.channelId

    val instance: com.zwolsman.blackjack.core.Game
        get() {
            val game = GameInstance(seed)
            for (i in 1..users.count()) {
                game.addPlayer(Player())
            }
            game.start()
            val options = history.split(",").filter { it.isNotBlank() }.map { Option.values()[it.toInt()] }
            for (option in options)
                game.currentPlayer?.currentHand?.playOption(option)

            return game
        }
}