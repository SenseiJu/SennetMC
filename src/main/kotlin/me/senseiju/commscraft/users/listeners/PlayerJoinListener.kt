package me.senseiju.commscraft.users.listeners

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.users.UserManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(plugin: CommsCraft, private val userManager: UserManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        if (userManager.userMap.containsKey(e.player.uniqueId)) return

        e.player.kickPlayer(("&8&lCommsCraft \n\n&cYour player data was not loaded before you connected " +
                "\nplease try and reconnect or contact an admin if this continues").color())
    }
}