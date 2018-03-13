package com.zwolsman.blackjack.discordbot.utils.formatters

import com.zwolsman.blackjack.core.Game
import com.zwolsman.blackjack.core.game.Hand
import com.zwolsman.blackjack.core.game.Player
import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.currentHand
import com.zwolsman.blackjack.discordbot.currentPlayer
import com.zwolsman.blackjack.discordbot.didWinOf
import com.zwolsman.blackjack.discordbot.entities.User
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.util.EmbedBuilder
import java.util.*
import kotlin.math.roundToLong

fun IChannel.sendMessage(game: com.zwolsman.blackjack.discordbot.entities.Game) {
    when (game.status) {
        0 -> openGame(game, this)
        1 -> playingGame(game, this)
        2 -> finishedGame(game, this)
        else -> TODO("Handle game status ${game.status}")
    }
}

private fun openGame(game: com.zwolsman.blackjack.discordbot.entities.Game, channel: IChannel) {
    val builder = EmbedBuilder()
            .withTitle(":game_die: Game ${game.id}")
            .withTimestamp(Date().toInstant())

    val players = transaction { game.users.map { it.user.mention to it.buyIn } }
    val spotsLeft = Config.maxPlayers - players.size
    if (spotsLeft == 0)
        builder.withDesc("The table is full! The game is about to start.")
    else
        builder.withDesc("This game is open and has **$spotsLeft ${if (spotsLeft == 1) "spot" else "spots"}** left. Be quick and join the game by typing `!join <buy-in>`")

    for ((player, buyIn) in players) {
        builder.appendField("Player", player, true)
        builder.appendField("Buy-in :moneybag:", buyIn.toString(), true)
        builder.emptyField()
    }

    channel.sendMessage(builder.build())
}

private fun EmbedBuilder.emptyField(inline: Boolean = true) = this.appendField("\u200E", "\u200E", inline)

private fun playingGame(game: com.zwolsman.blackjack.discordbot.entities.Game, channel: IChannel) {
    val builder = EmbedBuilder()
            .withTitle(":game_die: Game ${game.id}")
            .withTimestamp(Date().toInstant())

    builder.appendHand("Dealer", game.instance.dealer)

    transaction {
        builder.appendPlayers(game.users.map { it.user }.zip(game.instance.players), game.instance, channel)
    }

    channel.sendMessage(builder.build())
}


private fun finishedGame(game: com.zwolsman.blackjack.discordbot.entities.Game, channel: IChannel) {
    val builder = EmbedBuilder()
            .withTitle(":game_die: Game ${game.id}")
            .withTimestamp(Date().toInstant())
            .withDesc("This game is finished. ")

    val winners = mutableListOf<Pair<String, Long>>()
    transaction {
        val entities = game.users.zip(game.instance.players)
        for ((user, player) in entities) {
            for (hand in player.hands) {
                if (hand.didWinOf(game.instance.dealer)) {
                    val payout = if (hand.isBlackjack) 2.5 else 2.0
                    winners.add(user.user.mention to (user.buyIn * payout).roundToLong())
                }
            }
        }

        if (!winners.isEmpty()) {
            builder.appendField("Winners", winners.joinToString(separator = "\r\n") { it.first }, true)
            builder.appendField("Earnings", winners.joinToString(separator = "\r\n") { it.second.toString() }, true)
            builder.emptyField()
        } else {
            if (game.instance.dealer.isBlackjack) {
                builder.appendDesc("SIKE! Dealer got blackjack :money_with_wings: ")
            } else {
                builder.appendDesc("There are  no winners this round, only losers :rolling_eyes:")
            }
        }
        builder.appendHand("Dealer", game.instance.dealer)
        builder.appendPlayers(entities.map { it.first.user to it.second }, game.instance, channel)
    }

    channel.sendMessage(builder.build())
}

private fun EmbedBuilder.appendPlayers(zip: List<Pair<User, Player>>, game: Game, channel: IChannel) {
    for ((user, player) in zip) {
        val displayName = channel.guild.getUserByID(user.discordId).getDisplayName(channel.guild)

        if (player.hands.size == 1) {
            if (game.currentPlayer == player)
                appendHand("▪ $displayName", player.hands[0])
            else
                appendHand(displayName, player.hands[0])
            continue
        }
        for ((hid, hand) in player.hands.withIndex()) {
            if (game.currentPlayer?.currentHand == hand)
                appendHand("▪ $displayName hand ${hid + 1}", hand)
            else
                appendHand("$displayName hand ${hid + 1}", hand)
        }
    }
}

private fun EmbedBuilder.appendHand(name: String, hand: Hand) {
    this.appendField(name, hand.cards.joinToString { it.icon }, true)
    this.appendField("Points", hand.points.joinToString(), true)
    this.emptyField()
}
