package me.senseiju.sennetmc.users

import me.senseiju.sennetmc.SennetMC
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val upgradesFile = plugin.upgradesManager.upgradesFile

fun calculateMaxFishCapacity(upgrades: Int) : Int {
    return upgradesFile.config.getInt("fishing-capacity-starting-capacity", 100) +
            (upgradesFile.config.getInt("fishing-capacity-upgrade-increment", 15) * upgrades)
}

fun calculateSpeedboatSpeedMultiplier(upgrades: Int) : Double {
    return (upgradesFile.config.getDouble("speedboat-speed-upgrade-increment", 0.03) * upgrades) + 1
}

fun giveFishingRod(player: Player) {
    val rod = ItemStack(Material.FISHING_ROD)
    val rodMeta = rod.itemMeta
    rodMeta.isUnbreakable = true
    rod.itemMeta = rodMeta
    player.inventory.addItem(rod)
}