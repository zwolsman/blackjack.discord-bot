package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.command.Commands
import com.zwolsman.blackjack.discordbot.command.GlobalCommandListener
import com.zwolsman.blackjack.discordbot.currentGames
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class StartCommandListener : GlobalCommandListener(Commands.START) {
    override fun commandReceived(msg: String, event: MessageReceivedEvent) {
        val id = msg.substringAfter("start").trim().toInt() - 1
        val gameInstance = currentGames[id]
        if(gameInstance.game.isStarted) {
            event.channel.sendMessage("Game is already started!")
            return
        }
        gameInstance.game.start()

        gameInstance.sendAsMessage(event.client, event.channel)
    }

}