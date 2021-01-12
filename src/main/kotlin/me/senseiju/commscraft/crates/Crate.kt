package me.senseiju.commscraft.crates

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.commscraft.extensions.addItemOrDropNaturally
import me.senseiju.commscraft.extensions.color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class Crate(private val id: String, val name: String, description: List<String>, val upgradeId: String = "NULL",
            private val rewards: List<Reward>, val probabilityPerCast: Int = 0, private val maxCratesPerCast: Int = 0) {

    private val item = createItemStack(description)

    fun giveCrate(player: Player, amount: Int = 1) {
        player.inventory.addItemOrDropNaturally(item.clone().asQuantity(amount), player.location)
    }

    fun giveRandomNumberOfCrates(player: Player) {
        giveCrate(player, generateRandomNumberOfCrates())
    }

    private fun generateRandomNumberOfCrates() : Int {
        if (maxCratesPerCast <= 0) return 0
        return Random.nextInt(1, maxCratesPerCast + 1)
    }

    fun selectRandomReward() : Reward {
        val random = Random.nextInt(1, calculateProbabilityRange())
        var index = 0
        var probabilityCount = rewards[index].probability
        while (true) {
            if (random <= probabilityCount) {
                return rewards[index]
            }
            probabilityCount += rewards[++index].probability
        }
    }

    private fun calculateProbabilityRange() : Int {
        var range = 1
        rewards.forEach { range += it.probability }
        return range
    }

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