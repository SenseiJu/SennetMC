package me.senseiju.sennetmc.crates.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.crates.CratesManager
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.percentChance
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class PlayerFishListener(plugin: SennetMC, private val cratesManager: CratesManager) : Listener {

    private val users = plugin.userManager.userMap
    private val upgradesFile = plugin.upgradesManager.upgradesFile

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerFishEvent(e: PlayerFishEvent) {
        if (e.state != PlayerFishEvent.State.CAUGHT_FISH) {
            return
        }

        val user = users[e.player.uniqueId] ?: return

        if (!shouldPlayerReceiveCrates(user)) {
            return
        }

        val crate = cratesManager.selectRandomCrate(getPlayerCrateMasterProbabilityIncrease(user))
        val amount = crate.generateRandomNumberOfCrates()

        if (shouldPlayerReceiveDoubleCrates(user)) {
            crate.giveCrate(e.player, amount * 2)
        } else {
            crate.giveCrate(e.player, amount)
        }

        if (shouldCratesAutoCombine(user)) {
            cratesManager.combineCrates(e.player)
        }
    }

    private fun getPlayerCrateMasterProbabilityIncrease(user: User) : Double {
        return user.getUpgrade(Upgrade.CRATE_MASTER) * upgradesFile.config.getDouble("crate-master-upgrade-increment", 0.4)
    }

    private fun shouldPlayerReceiveCrates(user: User) : Boolean {
        val baseChance = cratesManager.cratesFile.config.getDouble("chance-to-receive-crates", 0.1)
        val discoveryChance = user.getUpgrade(Upgrade.DISCOVERY)
                .times(upgradesFile.config.getDouble("discovery-upgrade-increment", 0.01))

        return percentChance(baseChance + discoveryChance)
    }

    private fun shouldPlayerReceiveDoubleCrates(user: User) : Boolean {
        val treasureFinderChance = user.getUpgrade(Upgrade.TREASURE_FINDER)
                .times(upgradesFile.config.getDouble("treasure-finder-upgrade-increment", 0.01))

        return percentChance(treasureFinderChance)
    }

    private fun shouldCratesAutoCombine(user: User) : Boolean = user.getSetting(Setting.TOGGLE_AUTO_CRATE_COMBINING)
}