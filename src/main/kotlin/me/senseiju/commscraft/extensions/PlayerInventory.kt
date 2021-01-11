package me.senseiju.commscraft.extensions

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

fun PlayerInventory.addItemOrDropNaturally(item: ItemStack, location: Location) {
    if (this.firstEmpty() == -1) {
        location.world.dropItemNaturally(location, item)
    } else {
        this.addItem(item)
    }
}