package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.core.game.Player
import com.zwolsman.blackjack.discordbot.command.Commands
import com.zwolsman.blackjack.discordbot.command.GlobalCommandListener
import com.zwolsman.blackjack.discordbot.currentGames
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class JoinCommandListener : GlobalCommandListener(Commands.JOIN) {
    override fun commandReceived(msg: String, event: MessageReceivedEvent) {
        logger.info("Received join command with valid game id!")

        val id = msg.substringAfter("join").trim().toInt() - 1
        val gameInstance = currentGames[id]
        if (gameInstance.game.isStarted) {
            event.channel.sendMessage("Game is already started!")
            return
        }

        gameInstance.game.addPlayer(Player())
        gameInstance.players.add(event.author)

        gameInstance.sendAsMessage(event.channel)
    }
}