package com.zwolsman.blackjack.discordbot

import com.zwolsman.blackjack.core.Game
import com.zwolsman.blackjack.core.game.Status
import com.zwolsman.blackjack.discordbot.command.listeners.*
import org.slf4j.LoggerFactory
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IEmbed
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.MessageBuilder
import java.util.*

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
            registerListener(CreateGameCommandListener())
            registerListener(HitCommandListener())
            registerListener(SplitCommandListener())
            registerListener(StandCommandListener())
            registerListener(JoinCommandListener())
            registerListener(StartCommandListener())
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

//    fun sendAsMessage(client: IDiscordClient, channel: IChannel) {
//        var builder = MessageBuilder(client).withChannel(channel)
//
//        builder = builder.appendContent("**Game #$id**")
//        if (game.isFinished)
//            builder = builder.appendContent(" (FINISHED)")
//        else if (game.isStarted)
//            builder = builder.appendContent(" (STARTED)")
//        else if (!game.isStarted)
//            builder = builder.appendContent(" (OPEN)")
//
//        builder = builder.appendContent(" (${game.deck.seed})")
//        builder = builder.appendContent("\r\n")
//
//        //builder = builder.appendContent("_`seed ${game.deck.seed}`_\r\n")
//
//        builder = builder.appendContent("\tDealer _(${game.dealer.points.last()})_\r\n")
//        builder = builder.appendContent("\t\t${game.dealer.cards.joinToString { it.icon }}\r\n")
//        for ((pIndex, player) in game.players.withIndex()) {
//            builder = builder.appendContent("\t${players[pIndex].mention(true)} ")
//
//            if (player.hands.size == 1) {
//                val hand = player.hands[0]
//                if (game.isFinished)
//                    builder = builder.appendContent("_(${hand.points.last()})_")
//                else
//                    builder = builder.appendContent("_(${hand.points.joinToString()})_")
//
//                if (game.isFinished && hand.didWinOf(game.dealer))
//                    builder = builder.appendContent(" - \uD83D\uDCB0")
//
//                if (game.currentPlayer == player)
//                    builder = builder.appendContent(" ⬅")
//
//                builder = builder.appendContent("\r\n")
//                builder = builder.appendContent("\t\t${hand.cards.joinToString { it.icon }}\r\n")
//
//            } else {
//                builder = builder.appendContent("\r\n")
//                for ((hIndex, hand) in player.hands.withIndex()) {
//                    builder = builder.appendContent("\t\t")
//
//                    builder = builder.appendContent("Hand ${hIndex + 1} _(${hand.points.joinToString()})_")
//                    if (game.isFinished && hand.didWinOf(game.dealer))
//                        builder = builder.appendContent("\uD83D\uDCB0")
//                    if (hand == player.currentHand) {
//                        builder = builder.appendContent(" ⬅ ")
//                    }
//                    builder = builder.appendContent("\r\n")
//                    builder = builder.appendContent("\t\t${hand.cards.joinToString { it.icon }}\r\n")
//                    builder = builder.appendContent("\r\n")
//
//                }
//            }
//        }
//
//        builder.build()
//    }


    fun sendAsMessage(channel: IChannel) {
        var builder = EmbedBuilder()
                .withTimestamp(Date().toInstant())
                .withFooterText("⚜ Game $id")
                .withTitle("Game title")
                .withDesc("Game status: ${if (game.isFinished) "FINISHED" else if (game.isStarted) "STARTED" else "OPEN"}")
                .withColor(3619643)

        val emptyField = {
            builder = builder.appendField("\u200E", "\u200E", true)
        }

        builder = builder.appendField("Dealer", if(game.dealer.cards.size == 0) "none" else game.dealer.cards.joinToString { it.icon }, true)

        if (game.isFinished)
            builder = builder.appendField("Points", game.dealer.points.last().toString(), true)
        else
            builder = builder.appendField("Points", game.dealer.points.joinToString(), true)
        emptyField()

        for ((pid, player) in game.players.withIndex()) {
            val mention = players[pid].getDisplayName(channel.guild)
            if (player.hands.size == 1) {
                val hand = player.hands[0]

                val points = if (game.isFinished)
                    hand.points.last().toString()
                else
                    hand.points.joinToString()

                //append username
                if (game.currentPlayer == player)
                    builder = builder.appendField("$mention :arrow_left:", hand.cards.joinToString { it.icon }, true)
                else
                    builder = builder.appendField(mention, if(hand.cards.size == 0) "none" else hand.cards.joinToString { it.icon }, true)

                //append points
                if (game.isFinished && hand.didWinOf(game.dealer)) {
                    builder = builder.appendField("Points", "$points :tada:", true)
                } else {
                    if (hand.status == Status.BUSTED) {
                        builder = builder.appendField("Points", "$points :skull_crossbones:", true)
                    } else {
                        builder = builder.appendField("Points", points, true)
                    }
                }

                emptyField()

            } else {
                for ((hid, hand) in player.hands.withIndex()) {
                    val points = if (game.isFinished)
                        hand.points.last().toString()
                    else
                        hand.points.joinToString()

                    //append username with indicator
                    if (game.currentPlayer?.currentHand == hand) {
                        builder = builder.appendField("$mention hand ${hid + 1} :arrow_left:", if(hand.cards.size == 0) "none" else hand.cards.joinToString { it.icon }, true)
                    } else {
                        builder = builder.appendField("$mention hand ${hid + 1}", if(hand.cards.size == 0) "none" else hand.cards.joinToString { it.icon }, true)

                    }

                    if (game.isFinished && hand.didWinOf(game.dealer)) {
                        builder = builder.appendField("Points", "$points :tada:", true)
                    } else {
                        if (hand.status == Status.BUSTED) {
                            builder = builder.appendField("Points", "$points :skull_crossbones:", true)
                        } else {
                            builder = builder.appendField("Points", points, true)
                        }
                    }
                    emptyField()
                }
            }
        }

        channel.sendMessage(builder.build())
    }
}


val currentGames = arrayListOf<GameInstance>()