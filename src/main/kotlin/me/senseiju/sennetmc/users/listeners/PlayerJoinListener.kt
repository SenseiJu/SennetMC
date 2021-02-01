package me.senseiju.sennetmc.users.listeners

import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.color
import me.senseiju.sennetmc.models.listeners.playerPassengers
import me.senseiju.sennetmc.users.UserManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

class PlayerJoinListener(plugin: SennetMC, private val userManager: UserManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        if (userManager.userMap.containsKey(e.player.uniqueId)) {
            if (!e.player.inventory.contains(Material.FISHING_ROD)) {
                val rod = ItemStack(Material.FISHING_ROD)
                val rodMeta = rod.itemMeta
                rodMeta.isUnbreakable = true
                rod.itemMeta = rodMeta

                e.player.inventory.addItem(rod)
            }
            return
        }

        e.player.kickPlayer(("&8&lCommsCraft \n\n&cYour player data was not loaded before you connected " +
                "\nplease try and reconnect or contact an admin if this continues").color())
    }
}