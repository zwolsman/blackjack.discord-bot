package com.zwolsman.blackjack.discordbot.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable


object Games : IntIdTable() {
    val seed = long("seed")
    val history = text("history").default("")
    val status = integer("status").default(0)
    val channelId = long("channel_id")
}
class Game(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Game>(Games)

    val users by GamesUser referrersOn GamesUsers.user
    var seed by Games.seed
    var history by Games.history
    var status by Games.status
    var channelId by Games.channelId
}