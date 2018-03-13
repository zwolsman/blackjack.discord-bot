package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.command.GameAwareCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.HitCommand

class HitCommandListener : GameAwareCommandHandler<HitCommand>() {
    override val command = HitCommand()

    override fun commandReceived(command: HitCommand) {
        logger.info("Received hit command on game ${game.id}")
    }
}