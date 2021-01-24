package me.senseiju.sennetmc.fishes.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.color
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.fishes.FishType
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.users.calculateMaxFishCapacity
import me.senseiju.sennetmc.utils.percentChance
import org.bukkit.Sound
import org.bukkit.entity.FishHook
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class PlayerFishListener(plugin: SennetMC) : Listener {

    private val users = plugin.userManager.userMap
    private val upgradesFile = plugin.upgradesManager.upgradesFile

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerFishEvent(e: PlayerFishEvent) {
        val user = users[e.player.uniqueId] ?: return

        when (e.state) {
            PlayerFishEvent.State.FISHING -> onCast(e, user)
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
        val fishType = FishType.selectRandomType()

        if (shouldPlayerReceiveDoubleFish(user)) {
            user.addToCurrentFish(fishType, 2)
            e.player.sendConfigMessage("UPGRADES-DEUCE-PROCED", false)
        } else {
            user.addToCurrentFish(fishType)
        }

        if (shouldNearbyPlayersReceiveFish(user)) {
            giveNearbyPlayersFish(e.player, fishType)

            e.player.sendConfigMessage("UPGRADES-FEAST-PROCED", false)
        }

        sendCaughtFishMessage(user, e.player, fishType)

        e.expToDrop = 0
        e.caught?.remove()
    }

    private fun applyBaitUpgrade(user: User, hook: FishHook) {
        val baitUpgrade = user.getUpgrade(Upgrade.BAIT) * upgradesFile.config.getInt("bait-upgrade-increment", 10)

        if (baitUpgrade > 100) {
            hook.minWaitTime = 0
        } else {
            hook.minWaitTime = hook.minWaitTime - baitUpgrade
        }
        hook.maxWaitTime = hook.maxWaitTime - baitUpgrade
    }


    private fun shouldPlayerReceiveDoubleFish(user: User) : Boolean {
        val deuceChance = user.getUpgrade(Upgrade.DEUCE)
            .times(upgradesFile.config.getDouble("deuce-upgrade-increment", 0.01))

        return percentChance(deuceChance)
    }

    private fun shouldNearbyPlayersReceiveFish(user: User) : Boolean {
        val feastChance = user.getUpgrade(Upgrade.FEAST)
            .times(upgradesFile.config.getDouble("feast-upgrade-increment", 0.01))

        return percentChance(feastChance)
    }

    private fun giveNearbyPlayersFish(player: Player, fishType: FishType) {
        player.location.getNearbyPlayers(10.0).forEach {
            if (it.uniqueId == player.uniqueId) {
                return@forEach
            }

            if (!percentChance(upgradesFile.config.getDouble("feast-upgrade-random-player-chance", 0.8))) {
                return@forEach
            }

            val otherUser = users[it.uniqueId] ?: return@forEach
            otherUser.addToCurrentFish(fishType)

            it.sendConfigMessage("UPGRADES-FEAST-PROCED", false)
        }
    }

    private fun sendCaughtFishMessage(user: User, player: Player, fishType: FishType) {
        if (user.getSetting(Setting.TOGGLE_FISH_CAUGHT_MESSAGE)) {
            player.sendTitle("&bYou caught a &5${fishType.toString().toLowerCase()} &bfish".color(),
                "&6+${fishType.capacity()} capacity".color(), 10, 40, 10)
        }

        if (user.getSetting(Setting.TOGGLE_FISH_CAUGHT_SOUND)) {
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        }
    }
}