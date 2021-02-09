package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.color
import me.senseiju.sennetmc.extensions.defaultGuiTemplate
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.defaultScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val npc_type = NpcType.CHEF

private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val scheduler = plugin.server.scheduler
private val upgradesFile = plugin.upgradesManager.upgradesFile

fun openChefGui(player: Player) {
    defaultScope.launch {
        val gui = defaultGuiTemplate(3, npc_type.name)

        gui.setItem(2, 7, chefUpgradesGuiItem(player))
        gui.setItem(2, 3, chefSellGuiItem(player))

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun chefSellGuiItem(player: Player) : GuiItem {
    return ItemBuilder.from(Material.FURNACE)
        .setName("&b&lSell fish")
        .asGuiItem {

        }
}

private fun chefUpgradesGuiItem(player: Player) : GuiItem {
    return ItemBuilder.from(Material.CHEST)
        .setName("&b&lChef upgrades".color())
        .asGuiItem {
            openChefUpgradesGui(player)
        }
}

private fun openChefUpgradesGui(player: Player) {
    defaultScope.launch {
        val gui = defaultGuiTemplate(3, npc_type.name)

        gui.setCloseGuiAction {
            openChefGui(player)
        }

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}