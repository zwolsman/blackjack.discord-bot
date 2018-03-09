package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.command.Commands
import com.zwolsman.blackjack.discordbot.command.GlobalCommandListener
import com.zwolsman.blackjack.discordbot.service.GameService
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.*

class CreateGameCommandListener : GlobalCommandListener(Commands.CREATE) {
    override fun commandReceived(msg: String, event: MessageReceivedEvent) {
        logger.info("Received create command")
        val row = GameService.createGame(Random().nextLong(), event.author)
        logger.info("Created game, creator: ${event.author.name}!")
//        logger.info("For debugging purpose, starting the game!")
//        row.game.start()

        row.sendAsMessage(event.client, event.channel)
    }
}