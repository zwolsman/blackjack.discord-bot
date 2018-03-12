package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.command.BaseCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.CreateCommand
import com.zwolsman.blackjack.discordbot.entities.Game

class CreateCommandListener : BaseCommandHandler<CreateCommand>() {
    override fun commandReceived(command: CreateCommand) {
        logger.info("Yooooooo let's go!")
        logger.info("${command.user.name} has ${command.user.guildPoints} guild points and wants to create a game mannn")

        if(Game.isOpenIn(command.channel.longID)) {
            channel.sendMessage("There is already a game playing in this channel.")
            return
        }

        channel.sendMessage("Has to create a game for you")
    }

    override val command = CreateCommand()
}