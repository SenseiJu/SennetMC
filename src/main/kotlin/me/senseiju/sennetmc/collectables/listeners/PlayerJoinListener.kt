package me.senseiju.sennetmc.collectables.listeners

import me.senseiju.sennetmc.collectables.CollectablesManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val collectablesManager: CollectablesManager) : Listener {

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        val nameLength = e.player.name.length

        val collectableId = if (nameLength <= 3) "${nameLength}char" else null

        if (collectableId != null && !collectablesManager.collectables.containsKey(collectableId)) {
            collectablesManager.addCollectable(e.player.uniqueId, collectableId)
        }
    }
}