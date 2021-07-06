package me.senseiju.sennetmc.events.event.recycle

import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class RecycleListener(recycle: Recycle) : Listener {
    private val participants = recycle.participants

    @EventHandler
    private fun onPlayerCaughtFish(e: PlayerCaughtFishEvent) {
        participants.add(e.player.uniqueId)
    }
}