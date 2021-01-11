package me.senseiju.commscraft.users.listeners

import kotlinx.coroutines.launch
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.users.UserManager
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerPreLoginEvent(plugin: CommsCraft, private val userManager: UserManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerPreLogin(e: AsyncPlayerPreLoginEvent) {
        defaultScope.launch {
            if (e.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return@launch

            if (userManager.userMap.containsKey(e.uniqueId)) return@launch

            if (userManager.doesUserExist(e.uniqueId)) userManager.fetchUser(e.uniqueId)
            else userManager.createNewUser(e.uniqueId)

        }
    }
}