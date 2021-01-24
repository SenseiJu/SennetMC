package me.senseiju.commscraft.users

import me.senseiju.commscraft.CommsCraft
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(CommsCraft::class.java)
private val upgradesFile = plugin.upgradesManager.upgradesFile

fun calculateMaxFishCapacity(upgrades: Int) : Int {
    return upgradesFile.config.getInt("fishing_capacity-starting-capacity", 30) +
            (upgradesFile.config.getInt("fishing-capacity-upgrade-increment", 5) * upgrades)
}

fun calculateSpeedboatSpeedMultiplier(upgrades: Int) : Double {
    return (upgradesFile.config.getDouble("speedboat-speed-upgrade-increment", 0.03) * upgrades) + 1
}