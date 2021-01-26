package me.senseiju.sennetmc.events.event.shipwreck.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.event.shipwreck.Shipwreck
import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

class PlayerCaughtFishListener(plugin: SennetMC, private val shipwreck: Shipwreck) : Listener {

    private val cratesManager = plugin.cratesManager

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerCaughtFish(e: PlayerCaughtFishEvent) {
        if (shipwreck.finished) {
            HandlerList.unregisterAll(this)
            return
        }

        cratesManager.handleCratesOnFish(e.player)

        if (!shipwreck.participants.contains(e.player.uniqueId)) {
            shipwreck.participants.add(e.player.uniqueId)
        }
    }
}