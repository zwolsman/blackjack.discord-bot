package com.zwolsman.blackjack.discordbot.command

import org.slf4j.LoggerFactory
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

abstract class BaseCommandHandler<T : BaseCommand> : IListener<MessageReceivedEvent> {
    internal val logger = LoggerFactory.getLogger(this::class.java)!!

    abstract val command: T
    lateinit var channel: IChannel

    override fun handle(event: MessageReceivedEvent) {
        channel = event.channel
        if (command.parse(event)) {
            logger.info("Received ${command::class.java.simpleName} from ${event.author.getDisplayName(event.guild)} in ${event.guild.name}")
            channel.toggleTypingStatus()
            commandReceived(command)
        }
    }


    fun sendError(message: String) {
        channel.sendMessage(message)
    }

    abstract fun commandReceived(command: T)

}

