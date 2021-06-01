package me.senseiju.sennetmc.users.listeners

import kotlinx.coroutines.launch
import me.senseiju.sennetmc.users.UserManager
import me.senseiju.sennetmc.utils.defaultScope
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class PlayerPreLoginListener(private val userManager: UserManager) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onPlayerPreLogin(e: AsyncPlayerPreLoginEvent) {
        defaultScope.launch {
            if (e.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return@launch

            if (userManager.userMap.containsKey(e.uniqueId)) return@launch

            if (userManager.doesUserExist(e.uniqueId)) userManager.fetchUser(e.uniqueId)
            else userManager.createNewUser(e.uniqueId)
        }
    }
}