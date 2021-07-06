package me.senseiju.sennetmc.events.event.fishycollab

import me.senseiju.sennetmc.events.event.fishycollab.FishyCollab
import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class FishyListener(fishyCollab: FishyCollab) : Listener {
    private val participants = fishyCollab.participants

    @EventHandler
    private fun onPlayerCaughtFish(e: PlayerCaughtFishEvent) {
        participants.merge(e.player.uniqueId, 1, Int::plus)
    }
}