package me.senseiju.sennetmc.events.event.recycle

import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import me.senseiju.sennetmc.scrap.addScrap
import me.senseiju.sennetmc.utils.percentChance
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class RecycleListener(recycle: Recycle) : Listener {
    private val participants = recycle.participants
    private val collectChance: Double
    private val scrapRange: LongRange

    init {
        val config = recycle.eventsManager.eventsFile

        collectChance = config.getDouble("collect-chance", 0.65)
        scrapRange = config.getLong("scrap-minimum", 3)..config.getLong("scrap-maximum", 10)
    }

    @EventHandler
    private fun onPlayerCaughtFish(e: PlayerCaughtFishEvent) {
        participants.add(e.player.uniqueId)

        if (percentChance(collectChance)) {
            e.player.inventory.addScrap(scrapRange.random(), false)
        }
    }
}