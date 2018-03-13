package com.zwolsman.blackjack.discordbot.command.commands

import com.zwolsman.blackjack.discordbot.command.BaseCommand

class StandCommand : BaseCommand("stand", "s") {
    override val description = "Stands the current hand, can only be used by the player whose turn it is."
}