package com.zwolsman.blackjack.discordbot.command

import com.zwolsman.blackjack.discordbot.*
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.RequestBuffer


abstract class BaseCommandListener : IListener<MessageReceivedEvent>, HasLogger() {
    abstract val command: Commands
}

abstract class GlobalCommandListener(override val command: Commands) : BaseCommandListener() {
    override fun handle(event: MessageReceivedEvent) {
        if (event.author.isBot) {
            logger.info("Ignoring message ${event.messageID}, it is from a bot")
            return
        }
        val msg = event.message.content.toLowerCase().trim()
        if (command.value.matches(event.message)) {
            RequestBuffer.request {
                event.message.delete()
            }

            commandReceived(msg, event)
        }
    }

    abstract fun commandReceived(msg: String, event: MessageReceivedEvent)
}

abstract class InGameCommandListener(override val command: Commands) : BaseCommandListener() {
    override fun handle(event: MessageReceivedEvent) {
        if (event.author.isBot) {
            logger.info("Ignoring message ${event.messageID}, it is from a bot")
            return
        }
        if (command.value.matches(event.message)) {
            val gameInstance = currentGames.find { it.players.any { it == event.author } && !it.game.isFinished }
            if (gameInstance == null) {
                logger.error("Can't find game for ${event.author.name}")
                return
            }

            RequestBuffer.request {
                event.message.delete()
            }

            val playerId = gameInstance.players.indexOf(event.author)
            if (gameInstance.game.players[playerId] == gameInstance.game.currentPlayer) {
                logger.info("User ${event.author.name} played option \"${command}\" in game ${gameInstance.id}")
                commandReceived(gameInstance, playerId, event)
            } else {
                logger.error("Not the turn of ${event.author.mention()} in game ${gameInstance.id}!")
            }
        }
    }

    abstract fun commandReceived(gameInstance: GameInstance, playerId: Int, event: MessageReceivedEvent)
}