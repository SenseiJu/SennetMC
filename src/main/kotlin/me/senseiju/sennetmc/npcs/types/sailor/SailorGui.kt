package me.senseiju.sennetmc.npcs.types.sailor

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.guis.Gui
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.extensions.defaultGuiTemplate
import me.senseiju.sennetmc.npcs.calculateNextUpgradeCost
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.npcs.updateUpgradeGuiItem
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.defaultScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val npcType = NpcType.SAILOR
private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val upgradesFile = plugin.upgradesManager.upgradesFile

fun showSailorGui(player: Player) {
    defaultScope.launch {
        val user = plugin.userManager.userMap[player.uniqueId] ?: return@launch
        val gui = defaultGuiTemplate(3, npcType.npcName)

        gui.setItem(2, 5, createSpeedboatSpeedUpgradeGuiItem(gui, user))

        plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createSpeedboatSpeedUpgradeGuiItem(gui: Gui, user: User) : GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.SPEEDBOAT_SPEED)
    val upgradeMax = upgradesFile.config.getInt("speedboat-speed-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(upgradesFile.config.getDouble("speedboat-speed-upgrade-starting-cost", 300.0),
            currentUpgrades)

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&c&lREQUIRES A RANK")
    lore.add("")
    lore.add("&7Increase your boats speed when toggling")
    lore.add("&7to speedboat mode")
    lore.add("")
    lore.add("&7Cost: &e$$upgradeCost")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(gui, Material.OAK_BOAT, "&b&lSpeedboat speed", user, upgradeCost, upgradeMax, lore,
            Upgrade.SPEEDBOAT_SPEED) { createSpeedboatSpeedUpgradeGuiItem(gui, user) }
}