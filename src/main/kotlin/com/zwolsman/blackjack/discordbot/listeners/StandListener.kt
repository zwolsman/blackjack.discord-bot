package com.zwolsman.blackjack.discordbot.listeners

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.core.game.Status
import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.GameInstance
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class StandListener : InGameActionListener() {
    override fun commandReceived(msg: String, gameInstance: GameInstance, event: MessageReceivedEvent) {
        if (msg == "${Config.prefix}stand" || msg == "${Config.prefix}s") {
            val playerId = gameInstance.players.indexOf(event.author)
            gameInstance.game.players[playerId].hands.first { it.status == Status.OK }.playOption(Option.STAND)

            gameInstance.sendAsMessage(event.client, event.channel)
        }
    }
}