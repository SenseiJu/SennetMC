package me.senseiju.sennetmc.upgrades

import java.util.*
import javax.sql.rowset.CachedRowSet

enum class Upgrade(val databaseField: String, private val databaseFieldType: String) {
    /**
     * Fishmonger upgrades
     */
    FISH_CAPACITY("fish_capacity_upgrades", "INT"),
    FEAST("feast_upgrades", "INT"),
    LURE("lure_upgrades", "INT"),
    NEGOTIATE("negotiate_upgrades", "INT"),
    BAIT("bait_upgrades", "INT"),
    DEUCE("deuce_upgrades", "INT"),

    /**
     * Looter upgrades
     */
    TREASURE_FINDER("treasure_finder_upgrades", "INT"),
    DISCOVERY("discovery_upgrades", "INT"),
    CRATE_MASTER("crate_master_upgrades", "INT"),

    /**
     * Sailor upgrades
     */
    SPEEDBOAT_SPEED("speedboat_speed_upgrades", "INT"),

    /**
     * Chef upgrades
     */
    SEASONING("seasoning_upgrades", "INT"),
    SERVING_SPEED("serving_speed_upgrades", "INT"),

    /**
     * Not assigned
     */
    PLAYER_SPEED("player_speed_upgrades", "INT");

    companion object {
        private val queryValues = "?,".repeat(values().size).removeSuffix(",")
        private val queryFields = values().joinToString { "`${it.databaseField}`" }
        private val queryFieldsWithParameter = values().joinToString { "`${it.databaseField}`=?" }
        private val queryFieldsWithType = values().joinToString { "`${it.databaseField}` ${it.databaseFieldType}" }

        fun mapFromSet(set: CachedRowSet): EnumMap<Upgrade, Int> {
            return values().associateWith { set.getInt(it.databaseField) }
                .toMap(EnumMap<Upgrade, Int>(Upgrade::class.java))
        }

        fun buildCreateTableQuery(): String =
            "CREATE TABLE IF NOT EXISTS `upgrades`(`uuid` CHAR(36) NOT NULL, $queryFieldsWithType, UNIQUE(`uuid`));"

        fun buildUpdateQuery(): String =
            "INSERT INTO `upgrades`(`uuid`, $queryFields) VALUES(?, ${queryValues}) ON DUPLICATE KEY UPDATE $queryFieldsWithParameter;"
    }
}