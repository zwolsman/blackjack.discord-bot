package com.zwolsman.blackjack.discordbot.command.commands

import com.zwolsman.blackjack.discordbot.command.BaseCommand

class HitCommand : BaseCommand("hit", "h") {
    override val description = "Used to hit the current hand"
}