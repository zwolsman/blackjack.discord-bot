package com.zwolsman.blackjack.discordbot.command.listeners

import com.zwolsman.blackjack.core.game.Option
import com.zwolsman.blackjack.discordbot.GameInstance
import com.zwolsman.blackjack.discordbot.command.Commands
import com.zwolsman.blackjack.discordbot.command.InGameCommandListener
import com.zwolsman.blackjack.discordbot.currentHand
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

class HitCommandListener : InGameCommandListener(Commands.HIT) {
    override fun commandReceived(gameInstance: GameInstance, playerId: Int, event: MessageReceivedEvent) {
        logger.info("Received hit command")
        gameInstance.game.players[playerId].currentHand?.playOption(Option.HIT)
        gameInstance.sendAsMessage(event.client, event.channel)
    }
}