package me.senseiju.commscraft.npcs.types.looter.gui

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.guis.Gui
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.defaultGuiTemplate
import me.senseiju.commscraft.npcs.calculateNextUpgradeCost
import me.senseiju.commscraft.npcs.types.NpcType
import me.senseiju.commscraft.npcs.updateUpgradeGuiItem
import me.senseiju.commscraft.upgrades.Upgrade
import me.senseiju.commscraft.users.User
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val NPC_TYPE = NpcType.MERCHANT
private val NPC_CONFIG = NPC_TYPE.dataFile.config

private val plugin = JavaPlugin.getPlugin(CommsCraft::class.java)

fun showLooterGui(player: Player) {
    defaultScope.launch {
        val user = plugin.userManager.userMap[player.uniqueId]!!
        val gui = defaultGuiTemplate(3, NPC_TYPE.npcName)

        gui.setItem(2, 3, createTreasureFinderUpgradeGuiItem(gui, user))
        gui.setItem(2, 5, createDiscoveryUpgradeGuiItem(gui, user))
        gui.setItem(2, 7, createCrateMasterUpgradeGuiItem(gui, user))

        plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createTreasureFinderUpgradeGuiItem(gui: Gui, user: User) : GuiItem {
    val currentUpgrades = user.upgrades.computeIfAbsent(Upgrade.TREASURE_FINDER) { 0 }
    val upgradeIncrement = NPC_CONFIG.getDouble("treasure-finder-upgrade-increment", 0.01)
    val upgradeMax = NPC_CONFIG.getInt("treasure-finder-upgrade-max", 50)
    val upgradeCost = calculateNextUpgradeCost(NPC_CONFIG.getDouble("treasure-finder-upgrade-starting-cost", 300.0),
            currentUpgrades)

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the chance of getting double crates")
    lore.add("&7during a successful cast by &e$upgradeIncrement")
    lore.add("")
    lore.add("&7Cost: &e$$upgradeCost")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(gui, Material.CHEST, "&b&lTreasure finder", user, upgradeCost, upgradeMax, lore,
            Upgrade.TREASURE_FINDER) { createTreasureFinderUpgradeGuiItem(gui, user) }
}

private fun createDiscoveryUpgradeGuiItem(gui: Gui, user: User) : GuiItem {
    val currentUpgrades = user.upgrades.computeIfAbsent(Upgrade.DISCOVERY) { 0 }
    val upgradeIncrement = NPC_CONFIG.getDouble("discovery-upgrade-increment", 0.01)
    val upgradeMax = NPC_CONFIG.getInt("discovery-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(NPC_CONFIG.getDouble("discovery-upgrade-starting-cost", 300.0),
            currentUpgrades)

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the chance of successfully finding a")
    lore.add("&7crate when fishing by &e$upgradeIncrement")
    lore.add("")
    lore.add("&7Cost: &e$$upgradeCost")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(gui, Material.CHEST, "&b&lDiscovery", user, upgradeCost, upgradeMax, lore,
            Upgrade.DISCOVERY) { createDiscoveryUpgradeGuiItem(gui, user) }
}

private fun createCrateMasterUpgradeGuiItem(gui: Gui, user: User) : GuiItem {
    val currentUpgrades = user.upgrades.computeIfAbsent(Upgrade.CRATE_MASTER) { 0 }
    val upgradeIncrement = NPC_CONFIG.getDouble("crate-master-upgrade-increment", 0.25)
    val upgradeMax = NPC_CONFIG.getInt("crate-master-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(NPC_CONFIG.getDouble("crate-master-upgrade-starting-cost", 300.0),
            currentUpgrades)

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the chance of getting higher tier crates on")
    lore.add("&7a successful cast by a probability of &e$upgradeIncrement")
    lore.add("")
    lore.add("&7Cost: &e$$upgradeCost")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(gui, Material.CHEST, "&b&lCrate master", user, upgradeCost, upgradeMax, lore,
            Upgrade.CRATE_MASTER) { createCrateMasterUpgradeGuiItem(gui, user) }
}