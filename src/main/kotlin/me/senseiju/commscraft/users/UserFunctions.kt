package me.senseiju.commscraft.users

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.npcs.types.NpcType
import org.bukkit.plugin.java.JavaPlugin

private val MERCHANT_CONFIG = NpcType.MERCHANT.dataFile.config
private val SAILOR_CONFIG = NpcType.SAILOR.dataFile.config

fun calculateMaxFishCapacity(upgrades: Int) : Int {
    return MERCHANT_CONFIG.getInt("fishing_capacity-starting-capacity", 30) +
            (MERCHANT_CONFIG.getInt("fishing-capacity-upgrade-increment", 5) * upgrades)
}

fun calculateSpeedboatSpeedMultiplier(upgrades: Int) : Double {
    return (SAILOR_CONFIG.getDouble("speedboat-speed-upgrade-increment", 0.03) * upgrades) + 1
}