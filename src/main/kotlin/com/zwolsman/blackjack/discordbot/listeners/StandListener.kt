package com.zwolsman.blackjack.discordbot.listeners

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.GameInstance
import com.zwolsman.blackjack.discordbot.currentHand
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class StandListener : InGameActionListener() {
    override fun commandReceived(msg: String, gameInstance: GameInstance, playerId: Int, event: MessageReceivedEvent) {
        if (msg == "${Config.prefix}stand" || msg == "${Config.prefix}s") {
            gameInstance.game.players[playerId].currentHand?.playOption(Option.STAND)
            gameInstance.sendAsMessage(event.client, event.channel)
        }
    }
}