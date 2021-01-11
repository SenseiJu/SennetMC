package me.senseiju.commscraft.npcs.types.merchant

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.GuiAction
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.extensions.defaultGuiTemplate
import me.senseiju.commscraft.npcs.calculateNextUpgradeCost
import me.senseiju.commscraft.users.User
import me.senseiju.commscraft.utils.defaultScope
import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(CommsCraft::class.java)
private val econ = plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider

fun showMerchantUpgradeGui(player: Player) {
    defaultScope.launch {
        val user = plugin.userManager.userMap[player.uniqueId]!!
        val gui = defaultGuiTemplate(3, "&6&lMerchant")

        gui.setItem(2, 5, createFishingCapacityUpgradeGuiItem(user))

        plugin.server.scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createFishingCapacityUpgradeGuiItem(user: User) : GuiItem {
    val config = plugin.configFile.config

    val currentUpgrades = user.fishCapacityUpgrades
    val upgradeIncrement = config.getInt("fishes-upgrade-increment", 5)
    val upgradeMax = config.getInt("fishes-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(config.getDouble("fishes-upgrade-starting-cost", 300.0),
            currentUpgrades)

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7Increase your fishing capacity! Each upgrade")
    lore.add("&7increments your boat speed by &e$upgradeIncrement")
    lore.add("")
    lore.add("&7Cost: &e$$upgradeCost")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return ItemBuilder.from(Material.CHEST)
        .setName("&b&lFishing capacity".color())
        .setLore(lore.color())
        .asGuiItem(GuiAction { e ->
            if (e.whoClicked !is Player || econ == null || user.fishCapacityUpgrades >= upgradeMax) return@GuiAction

            val player = e.whoClicked as Player
            if (!econ.has(player, upgradeCost)) {
                player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
                return@GuiAction
            }

            econ.withdrawPlayer(player, upgradeCost)
            user.fishCapacityUpgrades += 1
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)

            showMerchantUpgradeGui(player)
        })
}