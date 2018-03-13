package com.zwolsman.blackjack.discordbot.command.commands

import com.zwolsman.blackjack.discordbot.command.BaseCommand

class PointsCommand : BaseCommand("points", "p") {
    override val description = "Show your current points in this guild."
}
