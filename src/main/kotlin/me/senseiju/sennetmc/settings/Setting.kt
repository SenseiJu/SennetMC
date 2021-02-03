package me.senseiju.sennetmc.settings

import java.util.*
import javax.sql.rowset.CachedRowSet

enum class Setting(val databaseField: String, private val databaseFieldType: String, val default: Boolean) {
    TOGGLE_AUTO_CRATE_COMBINING("toggle_auto_crate_combining", "TINYINT", true),
    TOGGLE_FISH_CAUGHT_SOUND("toggle_fish_caught_sound", "TINYINT", true),
    TOGGLE_FISH_CAUGHT_MESSAGE("toggle_fish_caught_message", "TINYINT",true),
    TOGGLE_NOTIFY_EVENT_MESSAGES("toggle_notify_event_messages", "TINYINT",true);

    companion object {
        private val queryValues = "?,".repeat(values().size).removeSuffix(",")
        private val queryFields = values().joinToString { "`${it.databaseField}`" }
        private val queryFieldsWithParameter = values().joinToString { "`${it.databaseField}`=?" }
        private val queryFieldsWithType = values().joinToString { "`${it.databaseField}` ${it.databaseFieldType}" }

        fun mapFromSet(set: CachedRowSet) : EnumMap<Setting, Boolean> {
            return values().associateWith { set.getBoolean(it.databaseField) }.toMap(EnumMap<Setting, Boolean>(Setting::class.java))
        }

        fun buildCreateTableQuery() : String =
                "CREATE TABLE IF NOT EXISTS `settings`(`uuid` CHAR(36) NOT NULL, $queryFieldsWithType, UNIQUE(`uuid`));"


        fun buildUpdateDatabaseQuery() : String =
                "INSERT INTO `settings`(`uuid`, ${queryFields}) VALUES(?, ${queryValues}) ON DUPLICATE KEY UPDATE ${queryFieldsWithParameter};"
    }
}