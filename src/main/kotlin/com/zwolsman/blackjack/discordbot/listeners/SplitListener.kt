package com.zwolsman.blackjack.discordbot.listeners

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.GameInstance
import com.zwolsman.blackjack.discordbot.currentHand
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class SplitListener : InGameActionListener() {
    override fun commandReceived(msg: String, gameInstance: GameInstance, playerId: Int, event: MessageReceivedEvent) {
        if (msg == "${Config.prefix}split") {
            gameInstance.game.players[playerId].currentHand?.playOption(Option.SPLIT)
            gameInstance.sendAsMessage(event.client, event.channel)
        }
    }
}