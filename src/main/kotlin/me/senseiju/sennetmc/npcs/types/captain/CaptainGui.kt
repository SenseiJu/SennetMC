package me.senseiju.sennetmc.npcs.types.captain

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.defaultScope
import me.senseiju.sennetmc.utils.extensions.defaultGuiTemplate
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.extensions.locationFromString
import me.senseiju.sentils.extensions.primitives.color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val npcType = NpcType.CAPTAIN
private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val warps = plugin.warpsFile
private val scheduler = plugin.server.scheduler

fun showCaptainGui(player: Player) {
    defaultScope.launch {
        val gui = defaultGuiTemplate(3, npcType.npcName)

        val warpsSection = warps.getConfigurationSection("captain-warps") ?: return@launch
        warpsSection.getKeys(false).forEach { warpName ->
            val warpSection = warpsSection.getConfigurationSection(warpName) ?: return@forEach

            val slot = warpSection.getInt("slot", 0)
            val name = warpSection.getString("name", "UNSET_NAME") ?: return@forEach
            val material = Material.matchMaterial(warpSection.getString("material", "BEDROCK")!!) ?: return@forEach
            val spawnPoint = locationFromString(warpSection.getString("spawn-point", null) ?: return@forEach)

            gui.setItem(slot, createWarpGuiItem(name, material, spawnPoint))
        }

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createWarpGuiItem(name: String, material: Material, spawnPoint: Location): GuiItem {
    return ItemBuilder.from(material)
        .setName(name.color())
        .asGuiItem { e ->
            if (isPlayerWarpingToSameWorld(e.whoClicked as Player, spawnPoint)) {
                e.whoClicked.sendConfigMessage(
                    "CAPTAIN-FAILED-TELEPORT-SAME-LOCATION", false,
                    PlaceholderSet("{captainName}", npcType.npcName)
                )
                e.whoClicked.closeInventory()
                return@asGuiItem
            }

            e.whoClicked.teleport(spawnPoint)
            e.whoClicked.sendConfigMessage(
                "CAPTAIN-TELEPORT-DESTINATION", false,
                PlaceholderSet("{destinationName}", name.color()),
                PlaceholderSet("{captainName}", npcType.npcName)
            )
            e.whoClicked.closeInventory()
        }
}

private fun isPlayerWarpingToSameWorld(player: Player, spawnPoint: Location): Boolean {
    return player.location.world.name == spawnPoint.world.name
}