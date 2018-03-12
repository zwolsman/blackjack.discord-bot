package com.zwolsman.blackjack.discordbot.models

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and

object Users : IntIdTable() {
    val name = text("name")
    val discordId = long("discord_user_id")
    val guildId = long("guild_id")
    val guildPoints = long("guild_points")
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users) {
        fun findInGuildAndChannel(guildId: Long, discordId: Long) = User.find { (Users.discordId eq discordId) and (Users.guildId eq guildId) }
    }

    var name by Users.name
    var discordId by Users.discordId
    val mention: String
        get() = "<@!$discordId>"
    var guildId by Users.guildId
    var guildPoints by Users.guildPoints
}

object UserRatings : IntIdTable() {
    val value = long("value")
    val film = reference("film", StarWarsFilms)
    val user = reference("user", Users)
}

object StarWarsFilms : IntIdTable() {
    val sequelId = integer("sequel_id").uniqueIndex()
    val name = varchar("name", 50)
    val director = varchar("director", 50)
}

class UserRating(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserRating>(UserRatings)

    var value by UserRatings.value
    var film by StarWarsFilm referencedOn UserRatings.film // use referencedOn for normal references
    var user by User referencedOn UserRatings.user
}

class StarWarsFilm(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StarWarsFilm>(StarWarsFilms)

    var name by StarWarsFilms.name
    var sequelId by StarWarsFilms.sequelId
    var director by StarWarsFilms.director
    val ratings by UserRating referrersOn UserRatings.film

}

//object Users : LongIdTable() {
//    val discordId = long("discord_id")
//    val guildId = long("guild_id")
//    val points = long("points")
//}
//
//object Games : IntIdTable() {
//    val hash = varchar("hash", 50)
//    val status = integer("status")
//    val channelId = long("channel_id")
//    val messageId = long("message_id").nullable()
//    val users by User referrersOn GamesUsers.user
//}
//
//object GamesUsers : LongIdTable() {
//    val user = reference("user", Users)
//    val game = reference("game", Games)
//}
//
//class Game(id: EntityID<Int>) : IntEntity(id) {
//    companion object : IntEntityClass<Game>(Games)
//
//    var hash by Games.hash
//    var status by Games.status
//    var channelId by Games.channelId
//    var messageId by Games.messageId
//
//    val game: com.zwolsman.blackjack.core.Game
//        get() = gameFromHash(hash)
//
//    private fun gameFromHash(hash: String): com.zwolsman.blackjack.core.Game {
//        val numbers = Hashids().decode(hash)
//        val game = com.zwolsman.blackjack.core.Game(numbers[0])
//        for (i in 1 until numbers.size) {
//            val optionId = numbers[i]
//            val option = Option.values()[optionId.toInt()]
//            game.currentPlayer?.currentHand?.playOption(option)
//        }
//        return game
//    }
//
//    fun addToHistory(option: Option) {
//        val numbers = Hashids().decode(hash)
//        hash = Hashids().encode(*numbers, option.ordinal.toLong())
//    }
//}
//
//class User(id: EntityID<Long>) : LongEntity(id) {
//    companion object : LongEntityClass<User>(Users)
//
//    val discordId by Users.discordId
//    val guildId by Users.guildId
//    val points by Users.points
//}

//
//object Cities: IntIdTable() {
//    val name = varchar("name", 50)
//}
//
//class User(id: EntityID<Int>) : IntEntity(id) {
//    companion object : IntEntityClass<User>(Users)
//
//    var name by Users.name
//    var city by City referencedOn Users.city
//    var age by Users.age
//}
//
//class City(id: EntityID<Int>) : IntEntity(id) {
//    companion object : IntEntityClass<City>(Cities)
//
//    var name by Cities.name
//    val users by User referrersOn Users.city
//}