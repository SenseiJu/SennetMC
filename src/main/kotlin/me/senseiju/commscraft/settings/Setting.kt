package me.senseiju.commscraft.settings

import java.util.*
import javax.sql.rowset.CachedRowSet

enum class Setting(val databaseField: String, val default: Boolean) {
    TOGGLE_AUTO_CRATE_COMBINING("toggle_auto_crate_combining", true);

    companion object {
        fun mapFromSet(set: CachedRowSet) : EnumMap<Setting, Boolean> {
            return values().associateWith { set.getBoolean(it.databaseField) }.toMap(EnumMap<Setting, Boolean>(Setting::class.java))
        }
    }
}