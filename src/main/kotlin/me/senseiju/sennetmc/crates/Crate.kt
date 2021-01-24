package me.senseiju.sennetmc.crates

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.sennetmc.extensions.addItemOrDropNaturally
import me.senseiju.sennetmc.extensions.color
import me.senseiju.sennetmc.utils.probabilityChance
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class Crate(private val id: String, val name: String, description: List<String>, val upgradeId: String = "NULL",
            private val rewards: List<Reward>, val probabilityPerCast: Double = 0.0, private val maxCratesPerCast: Int = 0) {

    private val item = createItemStack(description)

    fun giveCrate(player: Player, amount: Int = 1) {
        player.inventory.addItemOrDropNaturally(player.location, item.clone().asQuantity(amount))
    }


    fun generateRandomNumberOfCrates() : Int {
        if (maxCratesPerCast <= 0) return 0
        return Random.nextInt(1, maxCratesPerCast + 1)
    }

    fun selectRandomReward() : Reward = probabilityChance(rewards.map { it to it.probability }.toMap())

    private fun createItemStack(description: List<String>) : ItemStack {
        val descriptionFormatted = ArrayList<String>()
        description.forEach { descriptionLine ->
            if (!descriptionLine.contains("{rewards}")) {
                descriptionFormatted.add(descriptionLine)
                return@forEach
            }
            rewards.forEach { reward ->
                descriptionFormatted.add("&7- ${reward.name.color()}")
            }
        }
        val nbtItem = NBTItem(ItemBuilder.from(Material.CHEST)
            .glow(true)
            .setName(name.color())
            .setLore(descriptionFormatted.color())
            .build())
        nbtItem.setString("crate-id", id)

        return nbtItem.item
    }
}