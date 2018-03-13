package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.discordbot.command.UserAwareCommandHandler
import com.zwolsman.blackjack.discordbot.command.commands.PointsCommand
import com.zwolsman.blackjack.discordbot.entities.User

class PointsCommandListener : UserAwareCommandHandler<PointsCommand>() {
    override val command = PointsCommand()

    override fun commandReceived(command: PointsCommand) {

        val user = if (command.args.isEmpty()) {
            this.user
        } else {
            val name = command.args.joinToString(" ")
            if (name.startsWith("<@") && name.endsWith(">")) {
                User.findByDiscordId(name.substring(2 until name.length - 1).toLong(), channel.guild.longID)
            } else {
                User.findByName(name, channel.guild.longID)
            }
        }

        if (user == null) {
            channel.sendMessage("User _${command.args.joinToString(" ")}_ not in this guild.")
            return
        }

        val points = user.guildPoints
        val builder = StringBuilder()
        builder.append("${user.mention} currently has **$points server points**. ")
        if (points > 10000) {
            builder.append("You're in the 10k+ group! :money_mouth:")
        } else if (points > 5000) {
            builder.append("Do I smell money? :heavy_dollar_sign::heavy_dollar_sign::heavy_dollar_sign: ")
        } else if (points > 1000) {
            builder.append("1k baby :dollar: ")
        } else if (points > 500) {
            builder.append("You have more points that all the newbs. :money_mouth: ")
        } else {
            builder.append("You have less than you started with.. :poop:")
        }

        channel.sendMessage(builder.toString())
    }

}