package me.senseiju.sennetmc.scrap

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.extensions.addItemOrDropNaturally
import me.senseiju.sennetmc.utils.extensions.color
import me.senseiju.sennetmc.utils.extensions.decimalFormat
import me.senseiju.sennetmc.utils.extensions.forEachNotNullOrAir
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
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

fun ItemStack.isScrap(): Boolean {
    return NBTItem(this).hasKey(SCRAP_KEY)
}

fun ItemStack.getScrap(): Long {
    return NBTItem(this).getLong(SCRAP_KEY)
}

fun ItemStack.addScrap(amount: Long): ItemStack {
    return createScrapItem(NBTItem(this).getLong(SCRAP_KEY) + amount)
}

fun ItemStack.addScrap(toAdd: ItemStack): ItemStack {
    return addScrap(NBTItem(toAdd).getLong(SCRAP_KEY))
}

fun ItemStack.removeScrap(amount: Long): ItemStack {
    return createScrapItem(NBTItem(this).getLong(SCRAP_KEY) - amount)
}

private fun applyScrapAmountPlaceholder(scrapAmount: Long): String {
    return NAME.replace("{amount}", scrapAmount.decimalFormat()).color()
}

fun PlayerInventory.hasScrap(amount: Long): Boolean {
    storageContents.forEachNotNullOrAir { itemStack ->
        if (!itemStack.isScrap()) {
            return@forEachNotNullOrAir
        }

        if (itemStack.getScrap() < amount) {
            return@forEachNotNullOrAir
        }

        return true
    }

    return false
}

fun PlayerInventory.addScrap(amount: Long, newStack: Boolean = false) {
    if (!newStack) {
        storageContents.forEachNotNullOrAir { itemStack ->
            if (!itemStack.isScrap()) {
                return@forEachNotNullOrAir
            }

            remove(itemStack)
            addItemOrDropNaturally(itemStack.addScrap(amount))

            return
        }
    }

    addItemOrDropNaturally(createScrapItem(amount))
}

/**
 * Removes the amount of scrap if possible
 *
 * @param amount the amount to remove
 *
 * @return true if the removal was successful, false otherwise
 */
fun PlayerInventory.removeScrap(amount: Long): Boolean {
    storageContents.forEachNotNullOrAir { itemStack ->
        if (!itemStack.isScrap()) {
            return@forEachNotNullOrAir
        }

        val amountInItem = itemStack.getScrap()
        if (amountInItem < amount) {
            return@forEachNotNullOrAir
        }

        remove(itemStack)

        if (amountInItem != amount) {
            addItem(itemStack.removeScrap(amount))
        }

        return true
    }

    return false
}
