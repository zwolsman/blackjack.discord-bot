package com.zwolsman.blackjack.discordbot.command

import org.slf4j.LoggerFactory
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

abstract class BaseCommandHandler<T : BaseCommand> : IListener<MessageReceivedEvent> {
    internal val logger = LoggerFactory.getLogger(this::class.java)!!

    abstract val command: T
    override fun handle(event: MessageReceivedEvent) {
        if (command.parse(event)) {
            logger.info("Received ${command.aliases.first()} command from ${event.author.getDisplayName(event.guild)} in ${event.guild.name}")
            commandReceived(command)
        }
    }

    abstract fun commandReceived(command: T)

}

