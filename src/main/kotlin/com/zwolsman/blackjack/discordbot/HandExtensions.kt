package com.zwolsman.blackjack.discordbot

import com.zwolsman.blackjack.core.game.Hand
import com.zwolsman.blackjack.core.game.Player
import com.zwolsman.blackjack.core.game.Status

val Player.currentHand: Hand?
    get() = hands.firstOrNull { it.status == Status.OK }