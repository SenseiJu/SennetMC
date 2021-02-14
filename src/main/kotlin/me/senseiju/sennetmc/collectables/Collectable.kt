package me.senseiju.sennetmc.collectables

import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.sennetmc.Rarity
import me.senseiju.sennetmc.utils.extensions.color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class Collectable(private val id: String, val name: String, private val material: Material, private val glow: Boolean,
                  private val rarity: Rarity, description: List<String>) {

    val item = createItemStack(description)

    private fun createItemStack(description: List<String>) : ItemStack {
        val lore = ArrayList<String>()
        lore.add("&7ID: $id")
        lore.add("")
        lore.add("&bRarity: $rarity")
        lore.add("")
        lore.addAll(description)

        return ItemBuilder.from(material)
                .glow(glow)
                .setName(name.color())
                .setLore(lore.color())
                .build()
    }
}