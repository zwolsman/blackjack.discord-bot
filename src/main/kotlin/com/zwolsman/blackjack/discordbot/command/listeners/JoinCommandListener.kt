package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.command.UserAwareCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.JoinCommand
import com.zwolsman.blackjack.discordbot.entities.Game
import com.zwolsman.blackjack.discordbot.getMinimalBuyIn
import com.zwolsman.blackjack.discordbot.utils.formatters.sendMessage
import org.jetbrains.exposed.sql.transactions.transaction

class JoinCommandListener : UserAwareCommandHandler<JoinCommand>() {
    override val command = JoinCommand()

    override fun commandReceived(command: JoinCommand) {
        val game = Game.findInChannel(channel)

        if (game == null) {
            channel.sendMessage("There is no game in this channel.")
            return
        } else if (game.status != 0) {
            channel.sendMessage("The current game is already started. You'll have to wait.")
        }

        val minBuyIn = channel.getMinimalBuyIn()

        if (minBuyIn == null) {
            sendError("Invalid minimal buy in. Please use an other channel!")
            return
        }

        val buyIn = command.args.getOrNull(0)?.toIntOrNull() ?: minBuyIn


        if (buyIn > user.guildPoints) {
            sendError("I'm sorry ${user.mention}, you have insufficient points to join the game and buy in with **${buyIn} server points**.")
            return
        } else if (buyIn < minBuyIn) {
            sendError("Buy in amount is **too low**, the minimum is **$minBuyIn server points**")
            return
        }

        if (game.isFull) {
            sendError("This game is full. You'll have to wait for a new game to start.")
            return
        }

        logger.info("${user.name} will join game ${game.id} and buy in with $buyIn server points in guild ${channel.guild.name}")
        game.addUser(user, buyIn)
        if (game.isFull) {
            logger.info("$Game ${game.id} is full and will be started in guild ${channel.guild.name}")
            game.instance.start()
        }
        channel.sendMessage(game)
    }
}