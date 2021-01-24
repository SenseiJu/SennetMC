package me.senseiju.sennetmc.models.listeners

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.models.ModelType
import me.senseiju.sennetmc.models.ModelsManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.PlayerInventory

class HelmetListener(plugin: SennetMC, modelsManager: ModelsManager) : Listener {

    private val users = plugin.userManager.userMap
    private val models = modelsManager.models

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onHelmetUnequip(e: InventoryClickEvent) {
        if (e.clickedInventory !is PlayerInventory) {
            return
        }

        if (e.slot == 39) {
            e.isCancelled = true
        }
    }

    @EventHandler
    private fun onPlayerSpawn(e: PlayerPostRespawnEvent) {
        val user = users[e.player.uniqueId] ?: return
        val model = models[ModelType.HAT]?.get(user.activeModels[ModelType.HAT]) ?: return

        e.player.inventory.helmet = model.itemStack
    }

    @EventHandler
    private fun onPlayerDeath(e: PlayerDeathEvent) {
        e.drops.forEach {
            if (ModelType.isItemModel(it)) {
                it.amount = 0
            }
        }
    }
}