package me.senseiju.sennetmc.scrap.listeners

import me.senseiju.sennetmc.scrap.addScrap
import me.senseiju.sennetmc.scrap.isScrap
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class CombineScrapListener : Listener {

    @EventHandler
    private fun onScrapCombine(event: InventoryClickEvent) {
        val current = event.currentItem?.clone() ?: return
        val cursor = event.cursor?.clone() ?: return

        if (current.type == Material.AIR || cursor.type == Material.AIR) return
        if (!current.isScrap() || !cursor.isScrap()) return

        event.isCancelled = true
        event.cursor?.amount = 0
        event.currentItem = cursor.addScrap(current)

        if (event.whoClicked is Player) {
            (event.whoClicked as Player).playSound(
                event.whoClicked.location,
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1.0f,
                1.0f
            )
        }
    }

}