package me.senseiju.sennetmc.utils.extensions

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

val InventoryClickEvent.player: Player
    get() = whoClicked as Player