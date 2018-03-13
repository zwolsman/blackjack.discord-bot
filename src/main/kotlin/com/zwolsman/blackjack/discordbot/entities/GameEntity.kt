package com.zwolsman.blackjack.discordbot.entities

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.core.game.Player
import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.currentHand
import com.zwolsman.blackjack.discordbot.currentPlayer
import com.zwolsman.blackjack.discordbot.didWinOf
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import sun.plugin.dom.exception.InvalidStateException
import sx.blah.discord.handle.obj.IChannel
import kotlin.math.roundToInt
import kotlin.math.roundToLong


object Games : IntIdTable() {
    val seed = long("seed")
    val history = text("history").default("")
    val status = integer("status").default(0)
    val channelId = long("channel_id")
}

typealias GameInstance = com.zwolsman.blackjack.core.Game

class Game(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<Game>(Games) {
        fun isOpenIn(channelId: Long) = transaction { !Game.find { (Games.channelId eq channelId) and ((Games.status) eq 0 or (Games.status eq 1)) }.empty() }
        fun findInChannel(channelId: Long) = transaction { Game.find { (Games.status neq 2) and (Games.channelId eq channelId) }.firstOrNull() }
        fun findInChannel(channel: IChannel) = findInChannel(channel.longID)
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }

    fun addUser(user: User, buyIn: Int) {
        transaction {
            user.guildPoints -= buyIn
            GamesUser.new {
                this.user = user
                this.game = this@Game
                this.buyIn = buyIn
            }

            if (this@Game.isFull) {
                Game.logger.info("$Game ${this@Game.id} is full and will be started")
                status = 1
            }

        }
    }

    fun addMove(option: Option) {
        if (status != 1) {
            throw InvalidStateException("Game isn't in playing state. Can't play any options!")
        }
        transaction {
            history = (history.split(",").filter { !it.isBlank() } + listOf(option.ordinal)).joinToString(separator = ",")
            status = when {
                instance.isFinished -> 2
                instance.isStarted -> 1
                else -> 0
            }

            //Game is done
            if (status == 2) {
                val entities = users.zip(instance.players)
                for ((user, player) in entities) {
                    for (hand in player.hands) {
                        if (hand.didWinOf(instance.dealer)) {
                            val payout = (user.buyIn * if (hand.isBlackjack) 2.5 else 2.0).roundToInt()
                            user.user.guildPoints += payout
                            Game.logger.info("Game payed out $payout server points to ${user.user.name}")
                        }
                    }
                }
            }
        }

    }

    fun isTurnUser(user: User): Boolean {
        return transaction {

            val pairs = this@Game.users.zip(instance.players)
            val currentPlayer = instance.currentPlayer
            pairs.indexOfFirst { it.second == instance.currentPlayer && it.first.user.discordId == user.discordId }
        } != -1
    }


    val users by GamesUser referrersOn GamesUsers.game
    var seed by Games.seed
    var history by Games.history
    var status by Games.status
    var channelId by Games.channelId

    val instance: com.zwolsman.blackjack.core.Game
        get() {
            return transaction {
                val game = GameInstance(seed)
                for (i in 1..users.count()) {
                    game.addPlayer(Player())
                }
                game.start()
                val options = history.split(",").filter { it.isNotBlank() }.map { Option.values()[it.toInt()] }
                for (option in options)
                    game.currentPlayer?.currentHand?.playOption(option)

                game
            }
        }
    val isFull: Boolean
        get() = transaction { users.count() == Config.maxPlayers }
}