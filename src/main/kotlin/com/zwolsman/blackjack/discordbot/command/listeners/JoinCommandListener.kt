package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.command.Commands
import com.zwolsman.blackjack.discordbot.command.GlobalCommandListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class JoinCommandListener : GlobalCommandListener(Commands.JOIN) {
    override fun commandReceived(msg: String, event: MessageReceivedEvent) {
        logger.info("Received join command with valid game id!")
    }
}