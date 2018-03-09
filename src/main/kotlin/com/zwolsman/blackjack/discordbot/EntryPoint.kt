package com.zwolsman.blackjack.discordbot

import com.zwolsman.blackjack.core.Game
import com.zwolsman.blackjack.discordbot.listeners.*
import org.slf4j.LoggerFactory
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.MessageBuilder
import kotlin.concurrent.thread

object EntryPoint {

    val logger = LoggerFactory.getLogger(this::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            logger.error("Empty arguments! Please provide a token as argument")
            return
        }
        val client = createClient(args[0])
        client.dispatcher.run {
            registerListener(CreateGameListener())
            registerListener(StandListener())
            registerListener(HitListener())
            registerListener(ListOptionListener())
            registerListener(SplitListener())
        }
    }


    private fun createClient(token: String, login: Boolean = true): IDiscordClient {
        val builder = ClientBuilder().apply {
            withToken(token)
        }

        return if (login)
            builder.login()
        else
            builder.build()
    }
}

data class GameInstance(val game: Game, val players: ArrayList<IUser> = arrayListOf(), val id: Int = currentGames.size + 1) {

    fun sendAsMessage(client: IDiscordClient, channel: IChannel) {
        var builder = MessageBuilder(client).withChannel(channel)

        builder = builder.appendContent("**Game #$id**")
        if (game.isFinished)
            builder = builder.appendContent(" (FINISHED)")
        else if (game.isStarted)
            builder = builder.appendContent(" (STARTED)")

        builder = builder.appendContent("\r\n")

        builder = builder.appendContent("_`seed ${game.deck.seed}`_\r\n")

        builder = builder.appendContent("\tDealer _(${game.dealer.points.joinToString()})_\r\n")
        builder = builder.appendContent("\t\t_`${game.dealer.cards.joinToString { it.icon }}`_\r\n")
        for ((pIndex, player) in game.players.withIndex()) {
            builder = builder.appendContent("\tPlayer ${pIndex + 1} ")

            if (player.hands.size == 1) {
                val hand = player.hands[0]
                builder = builder.appendContent("_(${hand.points.joinToString()})_\r\n")
                builder = builder.appendContent("\t\t_`${hand.cards.joinToString { it.icon }}`_\r\n")

            } else {
                builder = builder.appendContent("\r\n")
                for ((hIndex, hand) in player.hands.withIndex()) {
                    builder = builder.appendContent("\t\t")
                    if (hand == player.currentHand) {
                        builder = builder.appendContent("**")
                    }
                    builder = builder.appendContent("Hand ${hIndex + 1} _(${hand.points.joinToString()})_")
                    if (hand == player.currentHand) {
                        builder = builder.appendContent("**")
                    }
                    builder = builder.appendContent("\r\n")
                    builder = builder.appendContent("\t\t`${hand.cards.joinToString { it.icon }}`\r\n")
                    builder = builder.appendContent("\r\n")

                }
            }
        }

        builder.build()
    }
}

val currentGames = arrayListOf<GameInstance>()