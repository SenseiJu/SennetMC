package me.senseiju.sennetmc.npcs

import me.mattstudios.mfgui.gui.components.GuiAction
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.Gui
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.extensions.color
import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.pow

private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val upgradesFile = plugin.upgradesManager.upgradesFile
private val econ = plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider

fun updateUpgradeGuiItem(
    gui: Gui,
    material: Material,
    name: String,
    user: User,
    upgradeCost: Double,
    upgradeMax: Int,
    lore: List<String>,
    upgrade: Upgrade,
    callback: Callback
): GuiItem {

    return ItemBuilder.from(material)
        .setName(name.color())
        .setLore(lore.color())
        .asGuiItem(GuiAction { e ->
            if (e.whoClicked !is Player || econ == null || user.upgrades[upgrade]!! >= upgradeMax) return@GuiAction

            val player = e.whoClicked as Player
            if (!econ.has(player, upgradeCost)) {
                player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
                return@GuiAction
            }

            econ.withdrawPlayer(player, upgradeCost)
            user.incrementUpgrade(upgrade)
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)

            gui.updateItem(e.slot, callback.invoke())
        })
}

fun interface Callback {
    fun invoke(): GuiItem
}

fun calculateNextUpgradeCost(
    baseCost: Double,
    currentUpgrades: Int,
    growthRate: Double = upgradesFile.config.getDouble("growth-rate", 1.3)
): Double {
    return "%.2f".format(baseCost * growthRate.pow(currentUpgrades)).toDouble()
}