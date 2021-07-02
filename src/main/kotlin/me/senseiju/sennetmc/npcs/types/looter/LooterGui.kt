package me.senseiju.sennetmc.npcs.types.looter

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.guis.Gui
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.calculateNextUpgradeCost
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.npcs.updateUpgradeGuiItem
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.defaultScope
import me.senseiju.sennetmc.utils.extensions.defaultGuiTemplate
import me.senseiju.sentils.extensions.primitives.asCurrencyFormat
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val npcType = NpcType.LOOTER
private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val upgradesFile = plugin.upgradesManager.upgradesFile

fun openLooterGui(player: Player) {
    defaultScope.launch {
        val user = plugin.userManager.userMap[player.uniqueId]!!
        val gui = defaultGuiTemplate(3, npcType.npcName)

        gui.setItem(2, 3, createTreasureFinderUpgradeGuiItem(gui, user))
        gui.setItem(2, 5, createDiscoveryUpgradeGuiItem(gui, user))
        gui.setItem(2, 7, createCrateMasterUpgradeGuiItem(gui, user))

        plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createTreasureFinderUpgradeGuiItem(gui: Gui, user: User): GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.TREASURE_FINDER)
    val upgradeMax = upgradesFile.config.getInt("treasure-finder-upgrade-max", 50)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("treasure-finder-upgrade-starting-cost", 300.0),
        currentUpgrades
    )

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the chance of getting double crates")
    lore.add("&7during a successful cast")
    lore.add("")
    lore.add("&7Cost: &e${upgradeCost.asCurrencyFormat("$")}")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(
        gui, Material.CHEST, "&b&lTreasure finder", user, upgradeCost, upgradeMax, lore,
        Upgrade.TREASURE_FINDER
    ) { createTreasureFinderUpgradeGuiItem(gui, user) }
}

private fun createDiscoveryUpgradeGuiItem(gui: Gui, user: User): GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.DISCOVERY)
    val upgradeMax = upgradesFile.config.getInt("discovery-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("discovery-upgrade-starting-cost", 300.0),
        currentUpgrades
    )

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the chance of successfully finding a")
    lore.add("&7crate when fishing")
    lore.add("")
    lore.add("&7Cost: &e${upgradeCost.asCurrencyFormat("$")}")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(
        gui, Material.CHEST, "&b&lDiscovery", user, upgradeCost, upgradeMax, lore,
        Upgrade.DISCOVERY
    ) { createDiscoveryUpgradeGuiItem(gui, user) }
}

private fun createCrateMasterUpgradeGuiItem(gui: Gui, user: User): GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.CRATE_MASTER)
    val upgradeMax = upgradesFile.config.getInt("crate-master-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("crate-master-upgrade-starting-cost", 300.0),
        currentUpgrades
    )

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the chance of getting higher tier")
    lore.add("&7crates on a successful cast")
    lore.add("")
    lore.add("&7Cost: &e${upgradeCost.asCurrencyFormat("$")}")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(
        gui, Material.CHEST, "&b&lCrate master", user, upgradeCost, upgradeMax, lore,
        Upgrade.CRATE_MASTER
    ) { createCrateMasterUpgradeGuiItem(gui, user) }
}