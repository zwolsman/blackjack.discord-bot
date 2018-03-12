package com.zwolsman.blackjack.discordbot.command.commands

import com.zwolsman.blackjack.discordbot.command.BaseCommand

class JoinCommand : BaseCommand("join", "j") {
    override val description = "Join a open game. Specify the ID of the game as argument"
}
