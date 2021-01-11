package me.senseiju.commscraft.crates.listeners

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.crates.CratesManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import kotlin.random.Random

class PlayerFishListener(private val plugin: CommsCraft, private val cratesManager: CratesManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerFishEvent(e: PlayerFishEvent) {
        val user = plugin.userManager.userMap[e.player.uniqueId] ?: return

        if (e.state == PlayerFishEvent.State.FISHING) onCast(e)
        else if (e.state == PlayerFishEvent.State.CAUGHT_FISH) onFishCaught(e)
    }

    private fun onCast(e: PlayerFishEvent) {
        if (e.isCancelled) return
    }

    private fun onFishCaught(e: PlayerFishEvent) {
        val random = Random.nextDouble(0.0, 1.0)
        if (random > cratesManager.cratesFile.config.getDouble("chance-to-receive-crates", 0.1)) return

        cratesManager.selectRandomCrate().giveRandomNumberOfCrates(e.player)
        cratesManager.combineCrates(e.player)
    }
}