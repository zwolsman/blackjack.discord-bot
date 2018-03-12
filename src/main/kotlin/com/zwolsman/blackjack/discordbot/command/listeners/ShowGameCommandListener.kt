package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.command.UserAwareCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.ShowGameCommand
import com.zwolsman.blackjack.discordbot.entities.Game
import com.zwolsman.blackjack.discordbot.sendMessage

class ShowGameCommandListener : UserAwareCommandHandler<ShowGameCommand>() {
    override val command = ShowGameCommand()

    override fun commandReceived(command: ShowGameCommand) {
        val game = Game.findInChannel(channel.longID)

        if (game == null) {
            channel.sendMessage("There is no game in this channel.")
        } else {
            channel.sendMessage(game)
        }

    }

}