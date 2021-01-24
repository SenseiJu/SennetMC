package me.senseiju.commscraft.models.listeners

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.models.ModelsManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.PlayerInventory

class SleeveListener(plugin: CommsCraft, modelsManager: ModelsManager) : Listener {

    private val users = plugin.userManager.userMap
    private val models = modelsManager.models

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerSwapHandItems(e: PlayerSwapHandItemsEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onPlayerSetOffHandItem(e: InventoryClickEvent) {
        if (e.clickedInventory !is PlayerInventory) {
            return
        }

        if (e.slot == 40) {
            e.isCancelled = true
        }
    }
}