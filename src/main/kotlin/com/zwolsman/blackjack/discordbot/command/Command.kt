package com.zwolsman.blackjack.discordbot.command

import com.zwolsman.blackjack.discordbot.Config
import sx.blah.discord.handle.obj.IMessage

sealed class Command {
    abstract val aliases: Array<out String>
    open fun matches(input: IMessage) = aliases.map { Config.prefix + it.toLowerCase().trim() }.any { it == input.content.toLowerCase() }
}

class GlobalCommand(override vararg val aliases: String) : Command()
open class InGameCommand(override vararg val aliases: String) : Command() {
    override fun matches(input: IMessage): Boolean {
        if (!super.matches(input))
            return false
        //TODO check if it matches a game
        return true
    }
}


enum class Commands(val value: Command) {
    HIT(InGameCommand("hit", "h")),
    STAND(InGameCommand("stand", "s")),
    SPLIT(InGameCommand("split")),
    CREATE(GlobalCommand("create"))
}