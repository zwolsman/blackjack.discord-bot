package com.zwolsman.blackjack.discordbot.command.commands

import com.zwolsman.blackjack.discordbot.command.BaseCommand

class ShowGameCommand : BaseCommand("game", "games", "g") {
    override val description = "Shows the current game that is being played in the channel."
}