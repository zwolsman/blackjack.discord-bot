package com.zwolsman.blackjack.discordbot.listeners

import com.zwolsman.blackjack.discordbot.Config
import com.zwolsman.blackjack.discordbot.GameInstance
import com.zwolsman.blackjack.discordbot.currentHand
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.MessageBuilder

class ListOptionListener : InGameActionListener() {
    override fun commandReceived(msg: String, gameInstance: GameInstance, playerId: Int, event: MessageReceivedEvent) {
        if (msg == "${Config.prefix}options") {
            val hand = gameInstance.game.players[playerId].currentHand
            val builder = MessageBuilder(event.client)
                    .withChannel(event.channel)
            if (hand != null) {
                builder
                        .appendContent("Your options on your current hand are:\r\n")
                        .appendContent("\t${hand.options.joinToString { Config.prefix + it.toString().toLowerCase() }}")
                        .build()
            } else {
                builder
                        .appendContent("You don't have a hand")
                        .build()
            }
        }
    }
}