package com.zwolsman.blackjack.discordbot.service

import com.zwolsman.blackjack.core.Game
import com.zwolsman.blackjack.core.game.Player
import com.zwolsman.blackjack.discordbot.GameInstance
import com.zwolsman.blackjack.discordbot.currentGames
import sx.blah.discord.handle.obj.IUser

object GameService {

    fun createGame(seed: Long, id: Int, author: IUser): GameInstance {
        val game = Game(seed)
        game.addPlayer(Player())

        val instance = GameInstance(game, id)
        instance.players.add(author)

        currentGames.add(instance)
        return instance
    }
}