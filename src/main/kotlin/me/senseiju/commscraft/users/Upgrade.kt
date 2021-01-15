package me.senseiju.commscraft.users

import java.util.*
import javax.sql.rowset.CachedRowSet

enum class Upgrade(val databaseField: String) {
    FISH_CAPACITY("fish_capacity_upgrades"),
    SPEEDBOAT_SPEED("speedboat_speed_upgrades"),
    TREASURE_FINDER("treasure_finder_upgrades"),
    DISCOVERY("discovery_upgrades"),
    CRATE_MASTER("crate_master_upgrades"),
    NEGOTIATE("negotiate_upgrades"),
    PLAYER_SPEED("player_speed_upgrades");

    companion object {
        fun mapFromSet(set: CachedRowSet) : EnumMap<Upgrade, Int> {
            return values().associateWith { set.getInt(it.databaseField) }.toMap(EnumMap<Upgrade, Int>(Upgrade::class.java))
        }
    }
}