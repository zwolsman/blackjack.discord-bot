package com.zwolsman.blackjack.discordbot

import com.zwolsman.blackjack.core.Game
import com.zwolsman.blackjack.core.game.Hand
import com.zwolsman.blackjack.core.game.Player
import com.zwolsman.blackjack.core.game.Status

val Player.currentHand: Hand?
    get() = hands.firstOrNull { it.status == Status.OK }
val Game.currentPlayer: Player?
    get() = if(isStarted) players.firstOrNull { it.hands.any { it.status == Status.OK } } else null

fun Hand.didWinOf(dealer: Hand) : Boolean {
    if(status == Status.BUSTED)
        return false
    if(points.last() > 21)
        return false
    if(dealer.status == Status.BUSTED)
        return true
    if(dealer.points.last() < points.last())
        return true

    println("Unhandeled case")
    return false
}