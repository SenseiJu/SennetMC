package me.senseiju.sennetmc.events.event.fishrace.listeners

import me.senseiju.sennetmc.events.event.fishrace.FishRace
import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

class PlayerCaughtFishListener(private val fishRace: FishRace) : Listener {
    private val playersFishCaught = fishRace.playersFishCaught

    @EventHandler
    private fun onPlayerCaughtFish(e: PlayerCaughtFishEvent) {
        if (fishRace.finished) {
            HandlerList.unregisterAll(this)
            return
        }

        playersFishCaught.merge(e.player.uniqueId, 1, Int::plus)
    }
}