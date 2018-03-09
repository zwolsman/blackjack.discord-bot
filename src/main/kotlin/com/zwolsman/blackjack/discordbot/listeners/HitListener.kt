package com.zwolsman.blackjack.discordbot.listeners

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.GameInstance
import com.zwolsman.blackjack.discordbot.currentHand
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class HitListener : InGameActionListener() {
    override fun commandReceived(msg: String, gameInstance: GameInstance, playerId: Int, event: MessageReceivedEvent) {
        if (msg == "${Config.prefix}hit" || msg == "${Config.prefix}h") {
            gameInstance.game.players[playerId].currentHand?.playOption(Option.HIT)

            gameInstance.sendAsMessage(event.client, event.channel)
        }
    }
}


