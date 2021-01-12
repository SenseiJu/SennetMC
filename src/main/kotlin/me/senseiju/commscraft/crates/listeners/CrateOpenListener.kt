package me.senseiju.commscraft.crates.listeners

import de.tr7zw.changeme.nbtapi.NBTItem
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.crates.CratesManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class CrateOpenListener(plugin: CommsCraft, private val cratesManager: CratesManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerRightClickCrate(e: PlayerInteractEvent) {
        if (!e.action.name.contains("RIGHT_CLICK") || e.item == null || e.material != Material.CHEST
            || e.hand == EquipmentSlot.OFF_HAND) return

        val nbtItem = NBTItem(e.item)
        if (!nbtItem.hasKey("crate-id")) return

        val crateId = nbtItem.getString("crate-id")

        cratesManager.cratesMap[crateId]?.selectRandomReward()?.executeCommands(e.player)

        e.player.inventory.itemInMainHand.amount = e.player.inventory.itemInMainHand.amount - 1
    }
}