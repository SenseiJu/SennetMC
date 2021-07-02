package me.senseiju.sennetmc.scrap

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sentils.extensions.color
import me.senseiju.sentils.extensions.entity.addItemOrDropNaturally
import me.senseiju.sentils.extensions.forEachNotNullOrAir
import me.senseiju.sentils.extensions.primitives.asCurrencyFormat
import me.senseiju.sentils.extensions.primitives.color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.*

private const val SCRAP_KEY = "scrap-amount"
private const val UUID_KEY = "uuid"
private const val NAME = "&e&l{amount} &d&lScrap"

private val lore = listOf(
    "",
    "&7You can use your scrap at ${NpcType.SCRAPPER.npcName}",
    "&7to craft cool rewards",
    "",
    "&7Tip: Combine scrap together by dragging and",
    "&7dropping onto another scrap item"
).color()

private fun createScrapItem(scrapAmount: Long): ItemStack {
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

private fun applyScrapAmountPlaceholder(scrapAmount: Long): String {
    return NAME.replace("{amount}", scrapAmount.asCurrencyFormat()).color()
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

/**
 * Checks if the player has enough scrap
 *
 * @param amount the required amount
 *
 * @return true if the player has enough, false otherwise
 */
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

/**
 * Adds scrap to the players inventory either as a new stack or to a previous item
 *
 * @param amount the amount to add
 * @param newStack whether the scrap to add should make a new item
 */
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
