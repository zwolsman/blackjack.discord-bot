package com.zwolsman.blackjack.discordbot.utils.formatters

import com.zwolsman.blackjack.discordbot.Config
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.util.EmbedBuilder
import java.util.*

fun IChannel.sendMessage(game: com.zwolsman.blackjack.discordbot.entities.Game) {
    when (game.status) {
        0 -> openGame(game, this)
        else -> TODO("Handle game status ${game.status}")
    }
}

private fun openGame(game: com.zwolsman.blackjack.discordbot.entities.Game, channel: IChannel) {
    val builder = EmbedBuilder()
            .withTitle(":game_die: Game ${game.id}")
            .withTimestamp(Date().toInstant())
    //.withDesc("This game is open and has ${}")

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

private fun playingGame(game: com.zwolsman.blackjack.discordbot.entities.Game) {

}