package com.zwolsman.blackjack.discordbot.command

import com.zwolsman.blackjack.discordbot.entities.User
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

abstract class UserAwareCommand(vararg aliases: String) : BaseCommand(*aliases) {

    lateinit var user: User
    override fun parse(event: MessageReceivedEvent): Boolean {
        if (!super.parse(event))
            return false

        val user = transaction {
            User.findInGuildAndChannel(event.guild.longID, event.author.longID).firstOrNull()
        }
        if (user == null) {
            logger.info("${event.author.getDisplayName(event.guild)} doesn't have an account yet. Let's create one!")
            this.user = transaction {
                User.new {
                    name = event.author.getDisplayName(event.guild)
                    discordId = event.author.longID
                    guildId = event.guild.longID
                    guildPoints = 500
                }
            }

        } else {
            this.user = user
        }

        return true
    }
}