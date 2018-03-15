package com.zwolsman.blackjack.discordbot.command.listeners.ingame

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.discordbot.command.GameAwareCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.SplitCommand
import com.zwolsman.blackjack.discordbot.utils.formatters.sendMessage

class SplitCommandListener : GameAwareCommandHandler<SplitCommand>() {
    override val command = SplitCommand()

    override fun commandReceived(command: SplitCommand) {
        //TODO refactor so it can be re-used
        if (game.status != 1) {
            logger.error("Game ${game.id} in ${channel.name} on ${channel.guild.name} is not started yet!")
            return
        }

        if (!game.isTurnUser(user)) {
            logger.error("It's not the turn of ${user.name}")
            return
        }
        game.addMove(Option.SPLIT)
        logger.info("Received split command on game ${game.id}")
        channel.sendMessage(game)
    }
}