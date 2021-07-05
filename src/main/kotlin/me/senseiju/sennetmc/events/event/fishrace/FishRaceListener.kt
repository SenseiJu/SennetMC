package me.senseiju.sennetmc.events.event.fishrace

import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

class FishRaceListener(fishRace: FishRace) : Listener {
    private val playersFishCaught = fishRace.playersFishCaught

    @EventHandler
    private fun onPlayerCaughtFish(e: PlayerCaughtFishEvent) {
        playersFishCaught.merge(e.player.uniqueId, 1, Int::plus)
    }
}