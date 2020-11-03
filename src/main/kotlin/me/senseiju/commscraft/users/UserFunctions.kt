package me.senseiju.commscraft.users

import me.senseiju.commscraft.CommsCraft
import org.bukkit.plugin.java.JavaPlugin

private val config = JavaPlugin.getPlugin(CommsCraft::class.java).configFile.config

fun calculateMaxFishCapacity(upgrades: Int) : Int {
    return config.getInt("starting-fish-capacity", 30) + config.getInt("fishes-upgrade-increment", 5) * upgrades
}