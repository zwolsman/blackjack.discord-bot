package com.zwolsman.blackjack.discordbot.command.commands

import com.zwolsman.blackjack.discordbot.command.BaseCommand

class CreateCommand : BaseCommand("create") {
    override val description = "Creates a game. You can specify a buy-in amount as argument otherwise it will take the minimum of the channel."
}