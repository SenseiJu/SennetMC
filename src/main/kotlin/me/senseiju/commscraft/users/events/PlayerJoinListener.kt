package me.senseiju.commscraft.users.events

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.users.UserManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val plugin: CommsCraft, private val userManager: UserManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (!e.player.hasPlayedBefore()) {
            userManager.createNewUser(e.player.uniqueId)
        } else if (!plugin.userManager.userMap.containsKey(e.player.uniqueId)) {
            userManager.createNewUser(e.player.uniqueId)
        }
    }
}