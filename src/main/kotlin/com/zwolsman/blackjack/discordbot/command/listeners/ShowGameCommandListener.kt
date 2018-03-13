package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.command.UserAwareCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.ShowGameCommand
import com.zwolsman.blackjack.discordbot.entities.Game
import com.zwolsman.blackjack.discordbot.isBlackjackChannel
import com.zwolsman.blackjack.discordbot.sendMessage

class ShowGameCommandListener : UserAwareCommandHandler<ShowGameCommand>() {
    override val command = ShowGameCommand()

    override fun commandReceived(command: ShowGameCommand) {
        val game = Game.findInChannel(channel.longID)

        if (game == null && channel.isBlackjackChannel) { //no current game in blackjack channel
            channel.sendMessage("There is no game in this channel.")
        } else if (game == null && !channel.isBlackjackChannel) { //other channel than a blackjack channel, show the current games that are being played
            val playingGames = channel.guild.channels.map { it.longID }.mapNotNull { Game.findInChannel(it) }
            channel.sendMessage(playingGames.joinToString(separator = ",\r\n") { "Game ${it.id.value} is playing in <#${it.channelId}>" })
        } else if (game != null) { //Show the current game in the channel
            channel.sendMessage(game)
        }

    }

}