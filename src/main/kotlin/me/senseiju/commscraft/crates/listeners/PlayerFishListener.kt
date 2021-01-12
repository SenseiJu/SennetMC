package me.senseiju.commscraft.crates.listeners

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.crates.CratesManager
import me.senseiju.commscraft.utils.percentChance
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import kotlin.random.Random

class PlayerFishListener(plugin: CommsCraft, private val cratesManager: CratesManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerFishEvent(e: PlayerFishEvent) {
        if (e.state == PlayerFishEvent.State.FISHING) onCast(e)
        else if (e.state == PlayerFishEvent.State.CAUGHT_FISH) onFishCaught(e)
    }

    private fun onCast(e: PlayerFishEvent) {
        if (e.isCancelled) return
    }

    private fun onFishCaught(e: PlayerFishEvent) {
        if (!percentChance(cratesManager.cratesFile.config.getDouble("chance-to-receive-crates", 0.1))) return

        cratesManager.selectRandomCrate().giveRandomNumberOfCrates(e.player)
        cratesManager.combineCrates(e.player)
    }
}