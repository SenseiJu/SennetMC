package me.senseiju.sennetmc.users.listeners

import me.senseiju.sennetmc.users.UserManager
import me.senseiju.sennetmc.users.giveFishingRod
import me.senseiju.sennetmc.utils.extensions.color
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val userManager: UserManager) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        if (!userManager.userMap.containsKey(e.player.uniqueId)) {
            e.player.kickPlayer(
                ("&#914ef5&lSennetMC \n\n&cYour player data was not loaded before you connected " +
                        "\nplease try and reconnect or contact an admin if this continues").color()
            )
        }

        if (!e.player.inventory.contains(Material.FISHING_ROD)) {
            giveFishingRod(e.player)
        }
    }
}