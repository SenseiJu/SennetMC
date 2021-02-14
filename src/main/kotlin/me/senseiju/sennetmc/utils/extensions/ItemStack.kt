package me.senseiju.sennetmc.utils.extensions

import org.bukkit.inventory.ItemStack

fun ItemStack.setCustomModelData(customModelData: Int) : ItemStack {
    val meta = this.itemMeta
    meta.setCustomModelData(customModelData)
    this.itemMeta = meta

    return this
}