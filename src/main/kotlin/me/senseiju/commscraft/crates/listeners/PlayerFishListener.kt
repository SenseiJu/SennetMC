package me.senseiju.commscraft.crates.listeners

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.fishes.FishType
import me.senseiju.commscraft.users.User
import me.senseiju.commscraft.users.calculateMaxFishCapacity
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class PlayerFishListener(private val plugin: CommsCraft) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerFishEvent(e: PlayerFishEvent) {
        val user = plugin.userManager.userMap[e.player.uniqueId] ?: return

        if (e.state == PlayerFishEvent.State.FISHING) onCast(e)
        else if (e.state == PlayerFishEvent.State.CAUGHT_FISH) onFishCaught(e, user)
    }

    private fun onCast(e: PlayerFishEvent) {
        if (e.isCancelled) return
    }

    private fun onFishCaught(e: PlayerFishEvent, user: User) {
        val fishType = FishType.selectRandomType()
        user.addToCurrentFish(fishType, 1)

        e.player.sendTitle("&bYou caught a &5${fishType.toString().toLowerCase()} &bfish".color(),
                "&6+${fishType.capacity()} capacity".color(), 10, 40, 10)
        e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)

        e.expToDrop = 0
        e.caught?.remove()
    }
}