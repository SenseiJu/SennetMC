package me.senseiju.sennetmc.fishes.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.fishes.FishType
import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.percentChance
import me.senseiju.sentils.extensions.primitives.color
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerCaughtFishListener(plugin: SennetMC) : Listener {

    private val users = plugin.userManager.userMap
    private val upgradesFile = plugin.upgradesManager.upgradesFile

    @EventHandler
    private fun onPlayerCaughtFish(e: PlayerCaughtFishEvent) {
        if (shouldPlayerReceiveDoubleFish(e.user)) {
            e.user.addToCurrentFish(e.fishType, 2)
            e.player.sendConfigMessage("UPGRADES-DEUCE-PROCED", false)
        } else {
            e.user.addToCurrentFish(e.fishType)
        }

        if (shouldNearbyPlayersReceiveFish(e.user)) {
            giveNearbyPlayersFish(e.player, e.fishType)

            e.player.sendConfigMessage("UPGRADES-FEAST-PROCED", false)
        }

        sendCaughtFishMessage(e.user, e.player, e.fishType)
    }

    private fun shouldPlayerReceiveDoubleFish(user: User): Boolean {
        val deuceChance = user.getUpgrade(Upgrade.DEUCE)
            .times(upgradesFile.config.getDouble("deuce-upgrade-increment", 0.01))

        return percentChance(deuceChance)
    }

    private fun shouldNearbyPlayersReceiveFish(user: User): Boolean {
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
            player.sendTitle(
                "&bYou caught a &5${fishType.toString().lowercase()} &bfish".color(),
                "&6+${fishType.capacity()} capacity".color(), 10, 40, 10
            )
        }

        if (user.getSetting(Setting.TOGGLE_FISH_CAUGHT_SOUND)) {
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        }
    }
}