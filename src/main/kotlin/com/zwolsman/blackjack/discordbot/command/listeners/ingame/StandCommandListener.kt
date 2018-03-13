package com.zwolsman.blackjack.discordbot.command.listeners.ingame

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.discordbot.command.GameAwareCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.StandCommand
import com.zwolsman.blackjack.discordbot.utils.formatters.sendMessage

class StandCommandListener : GameAwareCommandHandler<StandCommand>() {
    override val command = StandCommand()

    override fun commandReceived(command: StandCommand) {
        if (game.status != 1) {
            logger.error("Game ${game.id} in ${channel.name} on ${channel.guild.name} is not started yet!")
            return
        }

        if(!game.isTurnUser(user)) {
            logger.error("It's not the turn of ${user.name}")
            return
        }

        game.addMove(Option.STAND)
        logger.info("Received stand command on game ${game.id}")
        channel.sendMessage(game)
    }
}