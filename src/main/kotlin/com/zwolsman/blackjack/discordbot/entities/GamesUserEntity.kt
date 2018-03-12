package com.zwolsman.blackjack.discordbot.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object GamesUsers : IntIdTable() {
    val user = reference("user", Users)
    val game = reference("game", Games)
}

class GamesUser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GamesUser>(GamesUsers)

    var user by User referencedOn GamesUsers.user
    var game by Game referencedOn GamesUsers.game
}