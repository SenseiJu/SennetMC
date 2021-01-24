package me.senseiju.sennetmc.collectables.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.collectables.CollectablesManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(plugin: SennetMC, private val collectablesManager: CollectablesManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        val nameLength = e.player.name.length

        val collectableId = if (nameLength <= 3) "${nameLength}char" else null

        if (collectableId != null && !collectablesManager.collectables.containsKey(collectableId)) {
            collectablesManager.addCollectable(e.player.uniqueId, collectableId)
        }
    }
}