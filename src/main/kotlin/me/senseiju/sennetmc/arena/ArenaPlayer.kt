package me.senseiju.sennetmc.arena

import me.senseiju.sennetmc.extensions.addItemOrDropNaturally
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ArenaPlayer(val player: Player, val wager: List<ItemStack>) {

    val uuid = player.uniqueId

    var previousInventory: Array<ItemStack> = emptyArray()
    var previousLocation: Location? = null

    fun teleportToPreviousLocation() {
        player.teleport(previousLocation ?: return)
    }

    fun refundInventory() {
        player.inventory.contents = previousInventory
    }

    fun refundInventoryAndWager() {
        refundInventory()

        wager.forEach { player.inventory.addItemOrDropNaturally(player.location, it) }
    }

    fun refundInventoryWithWinnings(winnings: List<ItemStack>) {
        refundInventoryAndWager()

        winnings.forEach { player.inventory.addItemOrDropNaturally(player.location, it) }
    }

}