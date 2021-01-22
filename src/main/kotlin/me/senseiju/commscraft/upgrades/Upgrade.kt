package me.senseiju.commscraft.upgrades

import me.senseiju.commscraft.settings.Setting
import java.util.*
import javax.sql.rowset.CachedRowSet

enum class Upgrade(val databaseField: String, private val databaseFieldType: String) {
    FISH_CAPACITY("fish_capacity_upgrades", "INT"),
    SPEEDBOAT_SPEED("speedboat_speed_upgrades", "INT"),
    TREASURE_FINDER("treasure_finder_upgrades", "INT"),
    DISCOVERY("discovery_upgrades", "INT"),
    CRATE_MASTER("crate_master_upgrades", "INT"),
    NEGOTIATE("negotiate_upgrades", "INT"),
    PLAYER_SPEED("player_speed_upgrades", "INT");

    companion object {
        private val queryValues = "?,".repeat(values().size).removeSuffix(",")
        private val queryFields = values().joinToString { "`${it.databaseField}`" }
        private val queryFieldsWithParameter = values().joinToString { "`${it.databaseField}`=?" }
        private val queryFieldsWithType = values().joinToString { "`${it.databaseField}` ${it.databaseFieldType}" }

        fun mapFromSet(set: CachedRowSet) : EnumMap<Upgrade, Int> {
            return values().associateWith { set.getInt(it.databaseField) }.toMap(EnumMap<Upgrade, Int>(Upgrade::class.java))
        }

        fun buildCreateDatabaseQuery(): String =
                "CREATE TABLE IF NOT EXISTS `upgrades`(`uuid` CHAR(36) NOT NULL, $queryFieldsWithType, UNIQUE(`uuid`));"

        fun buildUpdateDatabaseQuery() : String =
                "INSERT INTO `upgrades`(`uuid`, $queryFields) VALUES(?, ${queryValues}) ON DUPLICATE KEY UPDATE $queryFieldsWithParameter;"
    }
}