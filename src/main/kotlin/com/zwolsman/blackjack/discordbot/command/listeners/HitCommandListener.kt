package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.discordbot.command.GameAwareCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.HitCommand
import com.zwolsman.blackjack.discordbot.utils.formatters.sendMessage

class HitCommandListener : GameAwareCommandHandler<HitCommand>() {
    override val command = HitCommand()

    override fun commandReceived(command: HitCommand) {
        if (game.status != 1) {
            logger.error("Game ${game.id} in ${channel.name} on ${channel.guild.name} is not started yet!")
            return
        }

        if(!game.isTurnUser(user)) {
            logger.error("It's not the turn of ${user.name}")
            return
        }
        game.addMove(Option.HIT)
        logger.info("Received hit command on game ${game.id}")
        channel.sendMessage(game)
    }
}