package me.senseiju.sennetmc.scrap

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.extensions.color
import me.senseiju.sennetmc.utils.extensions.decimalFormat
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

private const val SCRAP_KEY = "scrap-amount"
private const val UUID_KEY = "uuid"
private const val NAME = "&e&l{amount} &d&lScrap"

private val lore = listOf(
    "",
    "&fYou can use your scrap at ${NpcType.SCRAPPER.npcName}",
    "&fto craft cool rewards",
    "",
    "&7Tip: Combine scrap together by dragging and",
    "&7dropping onto another scrap item"
).color()

fun createScrapItem(scrapAmount: Long): ItemStack {
    val nbtItem = NBTItem(
        ItemBuilder.from(Material.RED_DYE)
            .setName(applyScrapAmountPlaceholder(scrapAmount))
            .setLore(lore)
            .build()
    )

    nbtItem.setLong(SCRAP_KEY, scrapAmount)
    nbtItem.setUUID(UUID_KEY, UUID.randomUUID())

    return nbtItem.item
}

fun addScrapAmountToItem(item: ItemStack, toAdd: ItemStack): ItemStack {
    return createScrapItem(NBTItem(item).getLong(SCRAP_KEY) + NBTItem(toAdd).getLong(SCRAP_KEY))
}

fun removeScrapAmountFromItem(item: ItemStack, toRemove: Long): ItemStack {
    return createScrapItem(NBTItem(item).getLong(SCRAP_KEY) - toRemove)
}

fun getScrapAmountFromItem(item: ItemStack): Long {
    return with(NBTItem(item)) {
        if (this.hasKey(SCRAP_KEY)) this.getLong(SCRAP_KEY) else 0
    }
}

fun isItemScrap(item: ItemStack): Boolean = NBTItem(item).hasKey(SCRAP_KEY)

private fun applyScrapAmountPlaceholder(scrapAmount: Long): String {
    return NAME.replace("{amount}", scrapAmount.decimalFormat()).color()
}
