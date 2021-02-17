package me.senseiju.sennetmc.crates.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.crates.CratesManager
import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.users.User
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerCaughtFishListener(plugin: SennetMC, private val cratesManager: CratesManager) : Listener {
    private val users = plugin.userManager.userMap

    @EventHandler
    private fun onPlayerFishEvent(e: PlayerCaughtFishEvent) {
        val user = users[e.player.uniqueId] ?: return

        cratesManager.handleCratesOnFish(e.player)

        if (shouldCratesAutoCombine(user)) {
            cratesManager.combineCrates(e.player)
        }
    }

    private fun shouldCratesAutoCombine(user: User) : Boolean = user.getSetting(Setting.TOGGLE_AUTO_CRATE_COMBINING)
}