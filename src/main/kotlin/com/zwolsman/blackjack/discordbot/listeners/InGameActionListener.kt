package com.zwolsman.blackjack.discordbot.listeners

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.discordbot.*
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.MessageBuilder

abstract class InGameActionListener : IListener<MessageReceivedEvent>, HasLogger() {

    private val commandos = listOf(listOf("options", "s", "h"), Option.values().map { it.toString() }).flatMap {
        it.map {
            Config.prefix + it.toLowerCase()
        }
    }

    override fun handle(event: MessageReceivedEvent) {
        if (event.author.isBot) {
            logger.info("Ignoring message ${event.messageID}, it is from a bot")
            return
        }
        val msg = event.message.content.toLowerCase().trim()
        if (!commandos.contains(msg)) {
            return
        }
        val gameInstance = currentGames.find { it.players.any { it == event.author } && !it.game.isFinished }

        if (gameInstance == null) {
            MessageBuilder(event.client)
                    .withChannel(event.channel)
                    .withContent("Can't find a game you are part of. Create a game or join one first!")
                    .build()
            logger.info("Can't find game info for ${event.author.name}!")
            return
        }
        val playerId = gameInstance.players.indexOf(event.author)

        if (gameInstance.game.players[playerId] == gameInstance.game.currentPlayer) {
            logger.info("User ${event.author.name} played option \"${msg.substring(Config.prefix.length)}\" in game ${gameInstance.id}")
            commandReceived(msg, gameInstance, playerId, event)

        } else {
            MessageBuilder(event.client)
                    .withChannel(event.channel)
                    .appendContent("It's not your turn ${event.author.name}!")
                    .build()
            logger.error("Not the turn of ${event.author.mention()} in game ${gameInstance.id}!")

        }
    }

    abstract fun commandReceived(msg: String, gameInstance: GameInstance, playerId: Int, event: MessageReceivedEvent)
}
