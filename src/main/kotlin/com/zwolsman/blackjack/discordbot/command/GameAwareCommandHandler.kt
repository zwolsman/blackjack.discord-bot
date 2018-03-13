package com.zwolsman.blackjack.discordbot.command

import com.zwolsman.blackjack.discordbot.entities.Game
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

abstract class GameAwareCommandHandler<T : BaseCommand> : UserAwareCommandHandler<T>() {
    lateinit var game: Game
    override fun handle(event: MessageReceivedEvent) {

        val game = Game.findInChannel(event.channel)

        if (game == null) {
            logger.error("#${channel.name} doesn't have a game in ${event.guild.name}!")
            return
        }
        this.game = game
        super.handle(event)
    }
}