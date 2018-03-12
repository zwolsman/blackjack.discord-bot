package com.zwolsman.blackjack.discordbot.listeners

import com.zwolsman.blackjack.discordbot.entities.Game
import com.zwolsman.blackjack.discordbot.entities.GamesUser
import com.zwolsman.blackjack.discordbot.entities.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.*

class BasicListener : IListener<MessageReceivedEvent> {

    private val logger = LoggerFactory.getLogger(this::class.java)!!
    override fun handle(event: MessageReceivedEvent) {

        val displayName = event.author.getDisplayName(event.guild)
        val channelName = event.channel.name

        if (event.author.isBot) {
            logger.debug("Ignoring message from $displayName in $channelName because it's a bot")
            return
        }

        val msg = event.message.content.toLowerCase().trim()


        if (msg == "!create") {
            logger.info("Received create event from $displayName in channel $channelName")

            var user = transaction { User.findInGuildAndChannel(event.guild.longID, event.author.longID).firstOrNull() }
            if (user == null) {
                logger.info("$displayName is not known in the db within this guild. Creating new user with 500 points")
                user = transaction {
                    User.new {
                        name = displayName
                        discordId = event.author.longID
                        guildId = event.guild.longID
                        guildPoints = 500
                    }
                }
            }

            logger.info("${user.name} has ${user.guildPoints} guild points!")

            val game = transaction {

                val game = Game.new {
                    channelId = event.channel.longID
                    seed = Random().nextLong()
                }
                GamesUser.new {
                    this.user = user
                    this.game = game
                }
                return@transaction game
            }
            logger.info(game.toString())
            event.channel.sendMessage("Created game bby")
        }

        if (msg == "!games") {
            val user = transaction { User.findInGuildAndChannel(event.guild.longID, event.author.longID).firstOrNull() }
            if (user == null) {
                event.channel.sendMessage("No games for you man. I'm sorry")
                return
            }

            val games = transaction {
                val filteredGames = user.games.filter { it.game.channelId == event.channel.longID }

                if (filteredGames.isEmpty())
                    "You don't have any games in this channel, I'm sorry ${user.mention}"
                else
                    "All your games in this channel: \r\n" + filteredGames.joinToString { "id: ${it.game.id.value}" }
            }
            event.channel.sendMessage(games)
        }
    }
}