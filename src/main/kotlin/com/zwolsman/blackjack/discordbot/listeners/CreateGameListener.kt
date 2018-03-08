package com.zwolsman.blackjack.discordbot.listeners

import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.HasLogger
import com.zwolsman.blackjack.discordbot.currentGames
import com.zwolsman.blackjack.discordbot.service.GameService
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.MessageBuilder

class CreateGameListener : IListener<MessageReceivedEvent>, HasLogger() {

    override fun handle(event: MessageReceivedEvent) {
        if (event.author.isBot) {
            logger.info("Ignoring message ${event.messageID}, it is from a bot")
            return
        }


        val msg = event.message.content.toLowerCase().trim()

        if (msg == "${Config.prefix}create") {

            val authorsGame = currentGames.find { it.players.any { it.longID == event.author.longID } && !it.game.isFinished }
            if (authorsGame != null) {
                MessageBuilder(event.client)
                        .withChannel(event.channel)
                        .withContent("Not so fast ${event.author.mention()}! You are already in game #${authorsGame.id}")
                        .build()
                return
            }

            val row = GameService.createGame(1, event.author)
            logger.info("Created game!")
            logger.info(row.game.toString())

            logger.info("For debugging purpose, starting the game!")
            row.game.start()

            MessageBuilder(event.client)
                    .withChannel(event.channel)
                    .withContent("Created game #${row.id} and added ${event.author.mention()} as participant")
                    .build()
        }
    }
}