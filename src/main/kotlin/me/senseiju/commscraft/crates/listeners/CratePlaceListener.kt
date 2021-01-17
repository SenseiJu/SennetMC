package me.senseiju.commscraft.crates.listeners

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.crates.CratesManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class CratePlaceListener(plugin: CommsCraft, private val cratesManager: CratesManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onCratePlace(e: BlockPlaceEvent) {
        if (cratesManager.isItemCrate(e.itemInHand)) e.isCancelled = true
    }
}