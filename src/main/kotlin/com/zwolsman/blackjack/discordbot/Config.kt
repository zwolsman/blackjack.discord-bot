package com.zwolsman.blackjack.discordbot

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

object Config {

    val tables: List<TableConfig>
    val prefix: String
    val maxPlayers: Int

    init {
        val mapper = ObjectMapper(YAMLFactory()).apply {
            this.propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
        }
        val config = mapper.readValue(this::class.java.getResourceAsStream("/default.config.yml"), ConfigWrapper::class.java)
        tables = config.tableConfig
        prefix = config.prefix
        maxPlayers = config.maxPlayers
    }

    private data class ConfigWrapper(val prefix: String = "", val maxPlayers: Int = 0, val tableConfig: List<TableConfig> = emptyList())

    data class TableConfig(val minBuyIn: Int = 0, val maxInstances: Int = 0)
}