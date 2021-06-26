package me.senseiju.sennetmc.equipment.fishing_net

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.sennetmc.equipment.Equipment
import me.senseiju.sentils.extensions.color
import me.senseiju.sentils.extensions.primitives.color
import org.bukkit.inventory.ItemStack

const val NET_NBT_KEY = "COSMO_FISHING_NET"

private val equipment = Equipment.FISHING_NET
private val fishingNetName = "&e&lFishing net".color()
private val fishingNetLore = listOf(
    "",
    "&fRight-click &7and aim towards the water to cast",
    "",
    "&c&lWARNING: &7If you throw the net at land or it hits land,",
    "&7you will lose it and not be refunded. If you throw it into",
    "&7shallow water, it may also fail. Don't miss click!"
).color()

fun createFishingNetItem(amount: Int = 1): ItemStack {
    val item = ItemBuilder.from(equipment.material)
        .setAmount(amount)
        .setName(fishingNetName)
        .setLore(fishingNetLore)
        .build()

    with (NBTItem(item)) {
        this.setBoolean(NET_NBT_KEY, true)

        return this.item
    }
}

fun isItemFishingNet(item: ItemStack): Boolean {
    if (item.type != equipment.material) {
        return false
    }

    with (NBTItem(item)) {
        if (!this.hasKey(NET_NBT_KEY)) {
            return false
        }
    }

    return true
}