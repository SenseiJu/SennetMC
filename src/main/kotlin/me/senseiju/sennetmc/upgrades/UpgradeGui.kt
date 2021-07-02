package me.senseiju.sennetmc.upgrades

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.PERMISSION_COMMANDS_FISHMONGER
import me.senseiju.sennetmc.PERMISSION_COMMANDS_LOOTER
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.npcs.types.fishmonger.openFishmongerGui
import me.senseiju.sennetmc.npcs.types.looter.openLooterGui
import me.senseiju.sennetmc.utils.defaultScope
import me.senseiju.sennetmc.utils.extensions.defaultGuiTemplate
import me.senseiju.sentils.extensions.color
import me.senseiju.sentils.extensions.entity.playSound
import me.senseiju.sentils.extensions.events.player
import me.senseiju.sentils.extensions.primitives.color
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val REQUIRES_HIGHER_RANK = "&c&l** REQUIRES HIGHER RANK **".color()

private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val scheduler = plugin.server.scheduler

fun openUpgradeGui(player: Player) {
    defaultScope.launch {
        val gui = defaultGuiTemplate(3, "&b&lUpgrades")

        gui.setItem(2, 4, createUpgradeRedirectItem(player, NpcType.FISHMONGER, Material.FISHING_ROD, PERMISSION_COMMANDS_FISHMONGER) { openFishmongerGui(player) })
        gui.setItem(2, 6, createUpgradeRedirectItem(player, NpcType.LOOTER, Material.CHEST, PERMISSION_COMMANDS_LOOTER) { openLooterGui(player) })

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createUpgradeRedirectItem(player: Player, npcType: NpcType, material: Material, permission: String, onSuccess: () -> Unit): GuiItem {
    return ItemBuilder.from(material)
        .setName(npcType.npcName)
        .setLore(createLore(player, permission))
        .asGuiItem {
            if (it.player.hasPermission(permission)) {
                onSuccess()
            } else {
                it.player.playSound(Sound.ENTITY_VILLAGER_NO)
            }
        }
}

private fun createLore(player: Player, permission: String): List<String> {
    val lore = arrayListOf("")
    if (!player.hasPermission(permission)) {
        lore.add(REQUIRES_HIGHER_RANK)
        lore.add("")
    }
    lore.add("&7If you do not have the required rank, you can still upgrade by")
    lore.add("&7visiting the NPCs around the island")

    return lore.color()
}