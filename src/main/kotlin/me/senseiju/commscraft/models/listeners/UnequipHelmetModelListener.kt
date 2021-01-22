package me.senseiju.commscraft.models.listeners

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.models.ModelType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.PlayerInventory

class UnequipHelmetModelListener(plugin: CommsCraft) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onHelmetUnequip(e: InventoryClickEvent) {
        if (e.slotType != InventoryType.SlotType.ARMOR || e.slot != 39 || e.currentItem == null) return

        e.isCancelled = ModelType.isItemModel(e.currentItem!!)
    }
}