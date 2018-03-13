package com.zwolsman.blackjack.discordbot.utils.formatters

import com.zwolsman.blackjack.core.game.Hand
import com.zwolsman.blackjack.discordbot.Config
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.util.EmbedBuilder
import java.util.*

fun IChannel.sendMessage(game: com.zwolsman.blackjack.discordbot.entities.Game) {
    when (game.status) {
        0 -> openGame(game, this)
        1 -> playingGame(game, this)
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

    if (game.history.isEmpty()) {
        builder.appendField("History", "None", false)
    } else {
        builder.appendField("History", game.history, false)
    }

    transaction {
        for ((user, player) in game.users.zip(game.instance.players)) {
            val displayName = channel.guild.getUserByID(user.user.discordId).getDisplayName(channel.guild)
            if (player.hands.size == 1) {
                val hand = player.hands[0]
                builder.appendHand(displayName, hand)
                continue
            }
            for ((hid, hand) in player.hands.withIndex()) {
                builder.appendHand("$displayName hand ${hid + 1}", hand)
            }
        }
    }

    channel.sendMessage(builder.build())
}

private fun EmbedBuilder.appendHand(name: String, hand: Hand) {

    this.appendField(name, hand.cards.joinToString() { it.icon }, true)
    this.appendField("Points", hand.points.joinToString(), true)

    this.emptyField()
}
