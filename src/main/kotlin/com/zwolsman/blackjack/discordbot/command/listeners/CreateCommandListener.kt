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
        logger.info("${user.name} has ${user.guildPoints} guild points and wants to create a game in ${channel.guild.name}")

        if (Game.isOpenIn(command.channel.longID)) {
            logger.error("There is already a game open #${channel.name}, ${channel.guild.name}")
            channel.sendMessage("There is already a game playing in this channel.")
            return
        }
        val minBuyIn = channel.getMinimalBuyIn()

        if (minBuyIn == null) {
            logger.error("Can't find minimum bet amount for ${channel.name} in ${channel.guild.name}")
            sendCorrectChannels()
            return
        }
        logger.info("The minimum buy in is $minBuyIn, parsed from ${channel.name} in guild ${channel.guild.name}")
        val buyIn = command.args.getOrNull(0)?.toIntOrNull() ?: minBuyIn

        if (buyIn > user.guildPoints) {
            sendError("I'm sorry ${user.mention}, you have insufficient points to create a game and buy in with **${buyIn} server points**.")
            logger.info("Buy in is too high for ${user.name} in ${channel.guild.name}")
            return
        } else if (buyIn < minBuyIn) {
            sendError("Buy in amount is **too low**, the minimum is **$minBuyIn server points**")
            logger.info("Buy in is too low, minimum is $minBuyIn #${channel.name}, ${channel.guild.name}")
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

        logger.info("Created a new game with id ${game.id.value}")

        channel.sendMessage("Created game with id ${game.id.value} and ${user.mention} bought in with **$buyIn server points**")
    }

    private fun sendCorrectChannels() {
        val channels = channel.guild.categories.flatMap { it.channels }.filter { it.getMinimalBuyIn() != null }.joinToString { it.mention() }
        sendError("Invalid channel to create a game, please use one of the following channels: $channels")
    }
}