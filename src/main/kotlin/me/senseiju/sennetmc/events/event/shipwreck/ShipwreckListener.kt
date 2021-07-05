package me.senseiju.sennetmc.events.event.shipwreck

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ShipwreckListener(plugin: SennetMC, shipwreck: Shipwreck) : Listener {
    private val cratesManager = plugin.cratesManager
    private val participants = shipwreck.participants

    @EventHandler
    private fun onPlayerCaughtFish(e: PlayerCaughtFishEvent) {
        cratesManager.handleCratesOnFish(e.player)

        if (!participants.contains(e.player.uniqueId)) {
            participants.add(e.player.uniqueId)
        }
    }
}