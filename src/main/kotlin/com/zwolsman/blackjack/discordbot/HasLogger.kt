package com.zwolsman.blackjack.discordbot

import org.slf4j.LoggerFactory

open class HasLogger {
    val logger = LoggerFactory.getLogger(this::class.java)
}