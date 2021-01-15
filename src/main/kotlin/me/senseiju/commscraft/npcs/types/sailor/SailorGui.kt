package me.senseiju.commscraft.npcs.types.sailor

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.guis.Gui
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.defaultGuiTemplate
import me.senseiju.commscraft.npcs.calculateNextUpgradeCost
import me.senseiju.commscraft.npcs.types.NpcType
import me.senseiju.commscraft.npcs.updateUpgradeGuiItem
import me.senseiju.commscraft.users.Upgrade
import me.senseiju.commscraft.users.User
import me.senseiju.commscraft.utils.defaultScope
import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val NPC_TYPE = NpcType.SAILOR
private val NPC_CONFIG = NPC_TYPE.dataFile.config

private val plugin = JavaPlugin.getPlugin(CommsCraft::class.java)

fun showSailorGui(player: Player) {
    defaultScope.launch {
        val user = plugin.userManager.userMap[player.uniqueId]!!
        val gui = defaultGuiTemplate(3, NPC_TYPE.npcName)

        gui.setItem(2, 5, createSpeedboatSpeedUpgradeGuiItem(gui, user))

        plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createSpeedboatSpeedUpgradeGuiItem(gui: Gui, user: User) : GuiItem {
    val currentUpgrades = user.upgrades.computeIfAbsent(Upgrade.SPEEDBOAT_SPEED) { 0 }
    val upgradeIncrement = NPC_CONFIG.getDouble("speedboat-speed-upgrade-increment", 0.01)
    val upgradeMax = NPC_CONFIG.getInt("speedboat-speed-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(NPC_CONFIG.getDouble("speedboat-speed-upgrade-starting-cost", 300.0),
            currentUpgrades)

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Upgrade your boats speed! Each upgrade")
    lore.add("&7increments your boat speed by &e$upgradeIncrement")
    lore.add("")
    lore.add("&7Cost: &e$$upgradeCost")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(gui, Material.OAK_BOAT, "&b&lSpeedboat speed", user, upgradeCost, upgradeMax, lore,
            Upgrade.SPEEDBOAT_SPEED) { createSpeedboatSpeedUpgradeGuiItem(gui, user) }
}