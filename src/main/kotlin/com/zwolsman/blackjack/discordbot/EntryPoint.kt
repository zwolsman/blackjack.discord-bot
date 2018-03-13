package com.zwolsman.blackjack.discordbot

import com.zwolsman.blackjack.core.Game
import com.zwolsman.blackjack.core.game.Status
import com.zwolsman.blackjack.discordbot.command.listeners.CreateCommandListener
import com.zwolsman.blackjack.discordbot.command.listeners.JoinCommandListener
import com.zwolsman.blackjack.discordbot.command.listeners.PointsCommandListener
import com.zwolsman.blackjack.discordbot.command.listeners.ShowGameCommandListener
import com.zwolsman.blackjack.discordbot.entities.Games
import com.zwolsman.blackjack.discordbot.entities.GamesUsers
import com.zwolsman.blackjack.discordbot.entities.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import java.util.*

object EntryPoint : HasLogger() {

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            logger.error("Empty arguments! Please provide a token as argument")
            return
        }
        setupDb()

        val client = createClient(args[0])
        client.dispatcher.run {
            registerListener(CreateCommandListener())
            registerListener(ShowGameCommandListener())
            registerListener(JoinCommandListener())
            registerListener(PointsCommandListener())
        }
    }


    private fun setupDb() {
        val jdbc = "jdbc:mysql://localhost:3306/blackjack"
        val driver = "com.mysql.jdbc.Driver"
        Database.connect(jdbc, driver, "blackjack", "zaQXZSKANp9zuP5W")
        transaction {
            logger.addLogger(StdOutSqlLogger)
            create(Users, Games, GamesUsers)
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

data class GameInstance(val game: Game, val id: Int, val players: ArrayList<IUser> = arrayListOf()) {

    var msgId: Long? = null

    private fun sendOpenGame(channel: IChannel) {

        val spots = 5
        val min = 5
        val max = 5

        val buyIn = 500

        val builder = EmbedBuilder()
                .withTimestamp(Date().toInstant())
                .withTitle(":game_die: Game $id")
                .withDesc("Game is open! There are `${spots - players.size}` spots left! The minimum is `$min server points` and maximum is `$max server points`")
                .withColor(234, 89, 110)
                .appendField("Current players", players.joinToString(separator = ", \r\n") { "${it.mention()} with a buy in of `$buyIn server points`" }, false)
        if (msgId != null) {
            channel.getMessageByID(msgId!!).edit(builder.build())
        } else {
            msgId = channel.sendMessage(builder.build()).longID
        }

    }

    private fun sendFinishedGame(channel: IChannel) {

        var builder = EmbedBuilder()
                .withTimestamp(Date().toInstant())
                .withTitle(":game_die: Game $id")
                .withColor(234, 89, 110)

        val emptyField = {
            builder = builder.appendField("\u200E", "\u200E", true)
        }

        val content = StringBuilder()
        for ((pid, player) in game.players.withIndex()) {
            val mention = players[pid].mention()
            if (player.hands.size == 1) {

                if (player.hands[0].didWinOf(game.dealer)) {
                    val payOutRate = if (player.hands[0].isBlackjack) 2.5 else 2.0
                    val payOut = 500 * payOutRate
                    content.appendln("$mention won `$payOut server points`")
                }
            } else {

                for ((hid, hand) in player.hands.withIndex()) {

                    if (hand.didWinOf(game.dealer)) {
                        val payOutRate = if (player.hands[0].isBlackjack) 2.5 else 2.0
                        val payOut = 500 * payOutRate

                        content.appendln("$mention hand ${hid + 1} won `$payOut server points`")
                    }
                }
            }
        }

        if (!content.isBlank()) {
            builder = builder.appendField("Earnings", content.toString(), false)
        } else {
            builder = builder.appendField("Earnings", "None", false)
        }


        builder = builder.appendField("Dealer", game.dealer.cards.joinToString { it.icon }, true)
        builder = builder.appendField("Points", "${game.dealer.points.last()} ${if (game.dealer.status == Status.BUSTED) ":boom:" else ""}", true)
        emptyField()


        for ((pid, player) in game.players.withIndex()) {
            val mention = players[pid].getDisplayName(channel.guild)
            if (player.hands.size == 1) {
                val hand = player.hands[0]

                val points = hand.points.last().toString()

                builder = builder.appendField(mention, hand.cards.joinToString { it.icon }, true)

                //append points
                if (hand.status == Status.BUSTED) {
                    builder = builder.appendField("Points", "$points :skull_crossbones:", true)
                } else if (hand.didWinOf(game.dealer)) {
                    builder = builder.appendField("Points", "$points :tada:", true)
                } else {
                    builder = builder.appendField("Points", points, true)
                }

                emptyField()

            } else {
                for ((hid, hand) in player.hands.withIndex()) {
                    val points = hand.points.joinToString()

                    builder = builder.appendField("$mention hand ${hid + 1}", hand.cards.joinToString { it.icon }, true)

                    if (hand.status == Status.BUSTED) {
                        builder = builder.appendField("Points", "$points :skull_crossbones:", true)
                    } else if (hand.didWinOf(game.dealer)) {
                        builder = builder.appendField("Points", "$points :tada:", true)
                    } else {
                        builder = builder.appendField("Points", points, true)
                    }

                    emptyField()
                }
            }
        }
        if (msgId != null) {
            val msg = channel.getMessageByID(msgId!!)
            msg.edit(builder.withDesc("Game is finished! The earnings are listed below.").build())
        } else {
            msgId = channel.sendMessage(builder.withDesc("Game is finished! The earnings are listed below.").build()).longID
        }
    }

    private fun sendPlayingGame(channel: IChannel) {
        var desc = "Game is started! There are a total of ${players.size} ${if (players.size == 1) "player" else "players"}. " +
                "The highest buy in is `500 server points` and the lowest `5 server points`. \r\n" +
                "It's the turn of "

        var builder = EmbedBuilder()
                .withTimestamp(Date().toInstant())
                .withTitle(":game_die: Game $id")
                .withColor(234, 89, 110)

        val emptyField = {
            builder = builder.appendField("\u200E", "\u200E", true)
        }

        builder = builder.appendField("Dealer", game.dealer.cards.joinToString { it.icon }, true)
        builder = builder.appendField("Points", game.dealer.points.joinToString(), true)
        emptyField()

        for ((pid, player) in game.players.withIndex()) {
            val mention = players[pid].getDisplayName(channel.guild)
            if (player.hands.size == 1) {
                val hand = player.hands[0]

                val points = hand.points.joinToString()

                //append username
                if (game.currentPlayer == player) {
                    desc += players[pid].mention()
                    builder = builder.appendField("$mention :arrow_left:", hand.cards.joinToString { it.icon }, true)
                } else
                    builder = builder.appendField(mention, hand.cards.joinToString { it.icon }, true)

                //append points
                if (hand.status == Status.BUSTED) {
                    builder = builder.appendField("Points", "$points :skull_crossbones:", true)
                } else {
                    builder = builder.appendField("Points", points, true)
                }


                emptyField()

            } else {
                for ((hid, hand) in player.hands.withIndex()) {
                    val points = hand.points.joinToString()

                    //append username with indicator
                    if (game.currentPlayer?.currentHand == hand) {
                        builder = builder.appendField("$mention hand ${hid + 1} :arrow_left:", if (hand.cards.size == 0) "none" else hand.cards.joinToString { it.icon }, true)
                    } else {
                        builder = builder.appendField("$mention hand ${hid + 1}", if (hand.cards.size == 0) "none" else hand.cards.joinToString { it.icon }, true)

                    }


                    if (hand.status == Status.BUSTED) {
                        builder = builder.appendField("Points", "$points :skull_crossbones:", true)
                    } else {
                        builder = builder.appendField("Points", points, true)
                    }

                    emptyField()
                }
            }
        }
        if (msgId != null) {
            channel.getMessageByID(msgId!!).edit(builder.build())
        } else {
            msgId = channel.sendMessage(builder.build()).longID
        }

    }

    fun sendAsMessage(channel: IChannel) {
        if (!game.isStarted) {
            sendOpenGame(channel)
            return
        } else if (game.isFinished) {
            sendFinishedGame(channel)
            return
        } else {
            sendPlayingGame(channel)
            return
        }
    }

}