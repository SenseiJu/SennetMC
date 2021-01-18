package me.senseiju.commscraft.crates.listeners

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.crates.CratesManager
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
        if (e.item == null || !cratesManager.isItemCrate(e.item!!) || !e.action.name.contains("RIGHT_CLICK")
                || e.hand == EquipmentSlot.OFF_HAND) return

        cratesManager.getCrateFromItem(e.item!!)?.selectRandomReward()?.executeCommands(e.player)

        e.player.inventory.itemInMainHand.amount = e.player.inventory.itemInMainHand.amount - 1

        e.isCancelled = true
    }
}