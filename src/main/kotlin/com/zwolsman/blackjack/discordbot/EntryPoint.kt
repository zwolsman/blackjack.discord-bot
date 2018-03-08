package com.zwolsman.blackjack.discordbot

import com.zwolsman.blackjack.core.Game
import com.zwolsman.blackjack.discordbot.listeners.CreateGameListener
import org.slf4j.LoggerFactory
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IUser

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

data class GameInstance(val game: Game, val players: ArrayList<IUser> = arrayListOf(), val id: Int = currentGames.size + 1)

val currentGames = arrayListOf<GameInstance>()