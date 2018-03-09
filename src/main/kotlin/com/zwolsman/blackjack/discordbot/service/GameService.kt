package com.zwolsman.blackjack.discordbot.service

import com.zwolsman.blackjack.core.Game
import com.zwolsman.blackjack.core.game.Player
import com.zwolsman.blackjack.discordbot.GameInstance
import com.zwolsman.blackjack.discordbot.currentGames
import sx.blah.discord.handle.obj.IUser

object GameService {

    fun createGame(seed: Long, author: IUser): GameInstance {
        val game = Game(1905955330393358675)
        game.addPlayer(Player())

        val instance = GameInstance(game)
        instance.players.add(author)

        currentGames.add(instance)
        return instance
    }
}