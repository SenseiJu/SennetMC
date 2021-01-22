package me.senseiju.commscraft.npcs.types.merchant

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

fun showMerchantGui(player: Player) {
    defaultScope.launch {
        val user = plugin.userManager.userMap[player.uniqueId] ?: return@launch
        val gui = defaultGuiTemplate(3, NPC_TYPE.npcName)

        gui.setItem(2, 5, createFishingCapacityUpgradeGuiItem(gui, user))

        plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createFishingCapacityUpgradeGuiItem(gui: Gui, user: User) : GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.FISH_CAPACITY)
    val upgradeIncrement = NPC_CONFIG.getInt("fishing-capacity-upgrade-increment", 5)
    val upgradeMax = NPC_CONFIG.getInt("fishing-capacity-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(NPC_CONFIG.getDouble("fishing-capacity-upgrade-starting-cost", 300.0),
            currentUpgrades)

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase your fishing capacity! Each upgrade")
    lore.add("&7increments your capacity by &e$upgradeIncrement")
    lore.add("")
    lore.add("&7Cost: &e$$upgradeCost")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(gui, Material.CHEST, "&b&lFishing capacity", user, upgradeCost, upgradeMax, lore,
            Upgrade.FISH_CAPACITY) { createFishingCapacityUpgradeGuiItem(gui, user) }
}