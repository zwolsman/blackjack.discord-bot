package com.zwolsman.blackjack.discordbot

import com.zwolsman.blackjack.core.Game
import com.zwolsman.blackjack.core.game.Hand
import com.zwolsman.blackjack.core.game.Player
import com.zwolsman.blackjack.core.game.Status

val Player.currentHand: Hand?
    get() = hands.firstOrNull { it.status == Status.OK }
val Game.currentPlayer: Player?
    get() = players.firstOrNull { it.hands.any { it.status == Status.OK } }