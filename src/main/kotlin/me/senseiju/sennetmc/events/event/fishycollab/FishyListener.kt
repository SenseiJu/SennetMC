package me.senseiju.sennetmc.events.event.fishycollab

import me.senseiju.sennetmc.events.event.fishycollab.FishyCollab
import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class FishyListener(private val fishyCollab: FishyCollab) : Listener {

    @EventHandler
    private fun onPlayerCaughtFish(e: PlayerCaughtFishEvent) {

    }
}