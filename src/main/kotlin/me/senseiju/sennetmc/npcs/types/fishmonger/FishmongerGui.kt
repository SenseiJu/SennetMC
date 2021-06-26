package me.senseiju.sennetmc.npcs.types.fishmonger

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

private val npcType = NpcType.FISHMONGER
private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val upgradesFile = plugin.upgradesManager.upgradesFile

fun openFishmongerGui(player: Player) {
    defaultScope.launch {
        val user = plugin.userManager.userMap[player.uniqueId] ?: return@launch
        val gui = defaultGuiTemplate(4, npcType.npcName)

        gui.setItem(2, 3, createFishingCapacityUpgradeGuiItem(gui, user))
        gui.setItem(2, 5, createDeuceUpgradeGuiItem(gui, user))
        gui.setItem(2, 7, createFeastUpgradeGuiItem(gui, user))
        gui.setItem(3, 3, createBaitUpgradeGuiItem(gui, user))
        gui.setItem(3, 5, createNegotiateUpgradeGuiItem(gui, user))
        gui.setItem(3, 7, createLureUpgradeGuiItem(gui, user))

        plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createFishingCapacityUpgradeGuiItem(gui: Gui, user: User): GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.FISH_CAPACITY)
    val upgradeMax = upgradesFile.config.getInt("fishing-capacity-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("fishing-capacity-upgrade-starting-cost", 300.0),
        currentUpgrades
    )

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase your fishing capacity")
    lore.add("")
    lore.add("&7Cost: &e${upgradeCost.asCurrencyFormat("$")}")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(
        gui, Material.CHEST, "&b&lFishing capacity", user, upgradeCost, upgradeMax, lore,
        Upgrade.FISH_CAPACITY
    ) { createFishingCapacityUpgradeGuiItem(gui, user) }
}

private fun createNegotiateUpgradeGuiItem(gui: Gui, user: User): GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.NEGOTIATE)
    val upgradeMax = upgradesFile.config.getInt("negotiate-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("negotiate-upgrade-starting-cost", 300.0),
        currentUpgrades
    )

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the amount your fish will sell")
    lore.add("")
    lore.add("&7Cost: &e${upgradeCost.asCurrencyFormat("$")}")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(
        gui, Material.GOLD_BLOCK, "&b&lNegotiate", user, upgradeCost, upgradeMax, lore,
        Upgrade.NEGOTIATE
    ) { createNegotiateUpgradeGuiItem(gui, user) }
}

private fun createBaitUpgradeGuiItem(gui: Gui, user: User): GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.BAIT)
    val upgradeMax = upgradesFile.config.getInt("bait-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("bait-upgrade-starting-cost", 300.0),
        currentUpgrades
    )

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the rate at which you catch fish")
    lore.add("")
    lore.add("&7Cost: &e${upgradeCost.asCurrencyFormat("$")}")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(
        gui, Material.APPLE, "&b&lBait", user, upgradeCost, upgradeMax, lore,
        Upgrade.BAIT
    ) { createBaitUpgradeGuiItem(gui, user) }
}

private fun createDeuceUpgradeGuiItem(gui: Gui, user: User): GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.DEUCE)
    val upgradeMax = upgradesFile.config.getInt("deuce-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("deuce-upgrade-starting-cost", 300.0),
        currentUpgrades
    )

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the chance of getting double fish")
    lore.add("&7during a successful cast")
    lore.add("")
    lore.add("&7Cost: &e${upgradeCost.asCurrencyFormat("$")}")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(
        gui, Material.TROPICAL_FISH, "&b&lDeuce", user, upgradeCost, upgradeMax, lore,
        Upgrade.DEUCE
    ) { createDeuceUpgradeGuiItem(gui, user) }
}

private fun createFeastUpgradeGuiItem(gui: Gui, user: User): GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.FEAST)
    val upgradeMax = upgradesFile.config.getInt("feast-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("feast-upgrade-starting-cost", 300.0),
        currentUpgrades
    )

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the chance to give random")
    lore.add("&7people near you a free fish of the")
    lore.add("&7same type on a successful cast")
    lore.add("")
    lore.add("&7Cost: &e${upgradeCost.asCurrencyFormat("$")}")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(
        gui, Material.COOKED_COD, "&b&lFeast", user, upgradeCost, upgradeMax, lore,
        Upgrade.FEAST
    ) { createFeastUpgradeGuiItem(gui, user) }
}

private fun createLureUpgradeGuiItem(gui: Gui, user: User): GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.LURE)
    val upgradeMax = upgradesFile.config.getInt("lure-upgrade-max", 25)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("lure-upgrade-starting-cost", 400.0),
        currentUpgrades
    )

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase the chance to get a larger")
    lore.add("&7fish more frequently")
    lore.add("")
    lore.add("&7Cost: &e${upgradeCost.asCurrencyFormat("$")}")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(
        gui, Material.FISHING_ROD, "&b&lLure", user, upgradeCost, upgradeMax, lore,
        Upgrade.LURE
    ) { createLureUpgradeGuiItem(gui, user) }
}