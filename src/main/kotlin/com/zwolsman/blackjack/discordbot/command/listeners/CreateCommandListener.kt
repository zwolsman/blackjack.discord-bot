package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.command.BaseCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.CreateCommand

class CreateCommandListener : BaseCommandHandler<CreateCommand>() {
    override fun commandReceived(command: CreateCommand) {
        logger.info("Yooooooo let's go!")
    }

    override val command = CreateCommand()
}