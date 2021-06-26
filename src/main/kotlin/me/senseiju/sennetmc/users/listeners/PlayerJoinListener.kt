package me.senseiju.sennetmc.users.listeners

import me.senseiju.sennetmc.users.UserManager
import me.senseiju.sennetmc.users.giveFishingRod
import me.senseiju.sentils.extensions.primitives.color
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val userManager: UserManager) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        if (!userManager.userMap.containsKey(e.player.uniqueId)) {
            e.player.kick(
                Component.text(
                    """
                        &#914ef5&lSennetMC 
                        
                        &cYour player data was not loaded before you connected 
                        &cplease try and reconnect or contact an admin if this continues
                    """.trimIndent().color()
                )
            )
        }

        if (!e.player.inventory.contains(Material.FISHING_ROD)) {
            giveFishingRod(e.player)
        }
    }
}