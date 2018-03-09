package com.zwolsman.blackjack.discordbot.command

import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.HasLogger
import com.zwolsman.blackjack.discordbot.currentGames
import sx.blah.discord.handle.obj.IMessage

sealed class Command : HasLogger() {
    abstract val aliases: Array<out String>
    open fun matches(input: IMessage) = matches(input.content)
    internal fun matches(input: String) = aliases.map { Config.prefix + it }.any { input.trim().equals(it, true) }
}

open class GlobalCommand(override vararg val aliases: String) : Command()
open class InGameCommand(override vararg val aliases: String) : Command() {
    override fun matches(input: IMessage): Boolean {
        if (!super.matches(input))
            return false
        //TODO check if it matches a game
        return true
    }
}

class JoinCommand : GlobalCommand("join") {
    override fun matches(input: IMessage): Boolean {
        val splitted = input.content.toLowerCase().split(" ")
        val cmd = splitted.first()

        if (!matches(cmd)) {
            return false
        }
        val args = splitted.subList(1, splitted.size)
        if (args.size != 1)
            return false

        val gameId = args[0].toIntOrNull()
        if (gameId == null) {
            logger.error("Argument is not an int!")
            logger.error("argument = ${args[0]}")
            return false
        }
        if(currentGames.size < gameId) {
            logger.error("Game id is invalid!")
            return false
        }

        return true
    }
}

class StartCommand : GlobalCommand("start") {
    override fun matches(input: IMessage): Boolean {
        val splitted = input.content.toLowerCase().split(" ")
        val cmd = splitted.first()

        if (!matches(cmd)) {
            return false
        }
        val args = splitted.subList(1, splitted.size)
        if (args.size != 1)
            return false

        val gameId = args[0].toIntOrNull()
        if (gameId == null) {
            logger.error("Argument is not an int!")
            logger.error("argument = ${args[0]}")
            return false
        }
        if(currentGames.size < gameId) {
            logger.error("Game id is invalid!")
            return false
        }

        return true
    }
}

enum class Commands(val value: Command) {
    HIT(InGameCommand("hit", "h")),
    STAND(InGameCommand("stand", "s")),
    SPLIT(InGameCommand("split")),

    //Global commands
    CREATE(GlobalCommand("create")),
    JOIN(JoinCommand()),
    START(StartCommand())
}