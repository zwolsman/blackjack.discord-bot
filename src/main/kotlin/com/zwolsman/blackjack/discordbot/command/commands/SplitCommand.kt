package com.zwolsman.blackjack.discordbot.command.commands

import com.zwolsman.blackjack.discordbot.command.BaseCommand

class SplitCommand : BaseCommand("split") {
    override val description = "Used to split the current hand. Only available if the 2 cards are the same rank."
}