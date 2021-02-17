package me.senseiju.sennetmc.fishes.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.fishes.FishType
import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.users.calculateMaxFishCapacity
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.probabilityChance
import org.bukkit.entity.FishHook
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class PlayerFishListener(private val plugin: SennetMC) : Listener {

    private val users = plugin.userManager.userMap
    private val upgradesFile = plugin.upgradesManager.upgradesFile

    @EventHandler
    private fun onPlayerFishEvent(e: PlayerFishEvent) {
        val user = users[e.player.uniqueId] ?: return

        when (e.state) {
            PlayerFishEvent.State.FISHING -> onCast(e, user)
            PlayerFishEvent.State.CAUGHT_ENTITY -> e.isCancelled = true
            PlayerFishEvent.State.CAUGHT_FISH -> onFishCaught(e, user)
            else -> return
        }
    }

    private fun onCast(e: PlayerFishEvent, user: User) {
        if (user.currentFishCaughtCapacity >= calculateMaxFishCapacity(user.getUpgrade(Upgrade.FISH_CAPACITY))) {
            e.player.sendConfigMessage("FISHING-MAX-FISH-CAPACITY-REACHED")
            e.isCancelled = true
        }

        applyBaitUpgrade(user, e.hook)
    }

    private fun onFishCaught(e: PlayerFishEvent, user: User) {
        val fishType = selectRandomFishType(getPlayerLureProbabilityIncrease(user))
        val event = PlayerCaughtFishEvent(e.player, user, fishType)

        plugin.server.pluginManager.callEvent(event)

        e.expToDrop = 0
        e.caught?.remove()
    }

    private fun selectRandomFishType(increasedProbability: Double = 0.0) : FishType =
        probabilityChance(FishType.values().map { it to (it.probability() + increasedProbability) }.toMap())

    private fun getPlayerLureProbabilityIncrease(user: User) : Double {
        return user.getUpgrade(Upgrade.LURE) * upgradesFile.config.getDouble("lure-upgrade-increment", 0.3)
    }

    private fun applyBaitUpgrade(user: User, hook: FishHook) {
        val baitUpgrade = user.getUpgrade(Upgrade.BAIT) * upgradesFile.config.getInt("bait-upgrade-increment", 10)

        if (hook.minWaitTime - baitUpgrade <= 0) {
            hook.minWaitTime = 0
        } else {
            hook.minWaitTime = hook.minWaitTime - baitUpgrade
        }

        if (hook.maxWaitTime - baitUpgrade <= 100) {
            hook.maxWaitTime = 100
        } else {
            hook.maxWaitTime = hook.maxWaitTime - baitUpgrade
        }
    }
}