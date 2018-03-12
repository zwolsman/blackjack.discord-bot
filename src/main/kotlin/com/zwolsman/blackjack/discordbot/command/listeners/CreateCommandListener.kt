package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.command.UserAwareCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.CreateCommand
import com.zwolsman.blackjack.discordbot.entities.Game
import com.zwolsman.blackjack.discordbot.getMinimalBuyIn
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class CreateCommandListener : UserAwareCommandHandler<CreateCommand>() {
    override val command = CreateCommand()

    override fun commandReceived(command: CreateCommand) {
        logger.info("Yooooooo let's go!")
        logger.info("${user.name} has ${user.guildPoints} guild points and wants to create a game mannn")

        if (Game.isOpenIn(command.channel.longID)) {
            channel.sendMessage("There is already a game playing in this channel.")
            return
        }
        val minBuyIn = channel.getMinimalBuyIn()

        if (minBuyIn == null) {
            sendError("Invalid channel to create a game, please use a channel that starts with `buy-in`")
            return
        }
        logger.info("The minimum buy in is $minBuyIn, parsed from ${channel.name} in guild ${channel.guild.name}")
        val buyIn = command.args.getOrNull(0)?.toIntOrNull() ?: minBuyIn

        if (buyIn > user.guildPoints) {
            sendError("I'm sorry ${user.mention}, you have insufficient points to create a game and buy in with **${buyIn} server points**.")
            return
        } else if (buyIn < minBuyIn) {
            sendError("Buy in amount is **too low**, the minimum is **$minBuyIn server points**")
            return
        }

        logger.info("${user.name} will create a game and buy in with $buyIn server points in guild ${channel.guild.name}")

        val game = transaction {
            val game = Game.new {
                channelId = channel.longID
                seed = Random().nextLong()
            }
            game.addUser(this@CreateCommandListener.user, buyIn)
            return@transaction game
        }

        logger.info("Created a new game")

        channel.sendMessage("Created game with id ${game.id.value} and ${user.mention} bought in with **$buyIn server points**")
    }
}