package com.zwolsman.blackjack.discordbot.command

import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.HasLogger
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

abstract class BaseCommand(vararg val aliases: String) : HasLogger() {

    lateinit var args: List<String>
    abstract val description: String
    
    open fun matches(cmd: String?): Boolean {
        return aliases.any { it.equals(cmd, true) }
    }

    lateinit var channel: IChannel

    open fun parse(event: MessageReceivedEvent): Boolean {
        val cmd = event.message.content.split(" ")
        if (cmd.isEmpty())
            return false

        val first = cmd.first()

        if (first.length <= Config.prefix.length)
            return false

        if (!first.startsWith(Config.prefix, true))
            return false

        if (!matches(first.substring(Config.prefix.length)))
            return false

        if (cmd.size > 1)
            args = cmd.subList(1, cmd.size)
        else
            args = emptyList()

        channel = event.channel
        return true
    }
}