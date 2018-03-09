package com.zwolsman.blackjack.discordbot.listeners

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.core.game.Status
import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.GameInstance
import com.zwolsman.blackjack.discordbot.HasLogger
import com.zwolsman.blackjack.discordbot.currentGames
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.MessageBuilder

abstract class InGameActionListener : IListener<MessageReceivedEvent>, HasLogger() {

    private val commandos = listOf(listOf("options"), Option.values().map { it.toString() }).flatMap {
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
        val gameInstance = currentGames.find { it.players.any { it == event.author } && !it.game.isFinished }

        if (gameInstance == null) {
            if (commandos.contains(msg))
                MessageBuilder(event.client)
                        .withChannel(event.channel)
                        .withContent("Can't find a game you are part of. Create a game or join one first!")
                        .build()
            return
        }
        val playerId = gameInstance.players.indexOf(event.author)

        commandReceived(msg, gameInstance, playerId, event)
    }

    abstract fun commandReceived(msg: String, gameInstance: GameInstance, playerId: Int, event: MessageReceivedEvent)
}