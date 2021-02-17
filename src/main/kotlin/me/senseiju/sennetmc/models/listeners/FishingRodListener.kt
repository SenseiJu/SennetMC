package me.senseiju.sennetmc.models.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.models.ModelType
import me.senseiju.sennetmc.utils.extensions.setCustomModelData
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerFishEvent

class FishingRodListener(plugin: SennetMC) : Listener {

    private val users = plugin.userManager.userMap

    @EventHandler
    private fun onPlayerFishEvent(e: PlayerFishEvent) {
        val modelData = users[e.player.uniqueId]?.activeModels?.get(ModelType.FISHING_ROD) ?: return

        if (modelData == -1) {
            return
        }

        if (e.state == PlayerFishEvent.State.REEL_IN) {
            e.player.inventory.setItemInMainHand(e.player.inventory.itemInMainHand.setCustomModelData(modelData))
        } else {
            e.player.inventory.setItemInMainHand(e.player.inventory.itemInMainHand.setCustomModelData(modelData + 1))
        }
    }

    @EventHandler
    private fun onPlayerDropFishingRod(e: PlayerDropItemEvent) {
        if (e.itemDrop.itemStack.type == Material.FISHING_ROD) {
            e.isCancelled = true
        }
    }
}