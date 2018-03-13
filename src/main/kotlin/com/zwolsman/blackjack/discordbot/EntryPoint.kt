package com.zwolsman.blackjack.discordbot

import com.zwolsman.blackjack.discordbot.command.listeners.CreateCommandListener
import com.zwolsman.blackjack.discordbot.command.listeners.JoinCommandListener
import com.zwolsman.blackjack.discordbot.command.listeners.PointsCommandListener
import com.zwolsman.blackjack.discordbot.command.listeners.ShowGameCommandListener
import com.zwolsman.blackjack.discordbot.command.listeners.ingame.HitCommandListener
import com.zwolsman.blackjack.discordbot.command.listeners.ingame.StandCommandListener
import com.zwolsman.blackjack.discordbot.entities.Games
import com.zwolsman.blackjack.discordbot.entities.GamesUsers
import com.zwolsman.blackjack.discordbot.entities.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient

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
            registerListener(HitCommandListener())
            registerListener(StandCommandListener())
        }
    }


    private fun setupDb() {
        val jdbc = "jdbc:mysql://localhost:3306/blackjack"
        val driver = "com.mysql.cj.jdbc.Driver"
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