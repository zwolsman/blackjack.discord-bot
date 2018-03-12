package com.zwolsman.blackjack.discordbot.listeners

import com.zwolsman.blackjack.discordbot.models.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

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

            event.channel.sendMessage("Created game bby")
        }
    }
}