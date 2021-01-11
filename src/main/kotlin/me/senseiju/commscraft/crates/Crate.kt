package me.senseiju.commscraft.crates

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.commscraft.extensions.color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class Crate(val id: String, val name: String, description: List<String>, val cratesRequiredToUpgrade: Int = 4,
            val upgradeId: String? = null) {

    var rewards = ArrayList<Reward>()
    private val crateItemStack: ItemStack

    init {
        crateItemStack = createCrateItemStack(description)
    }

    fun giveCrate(player: Player, amount: Int = 1) {
        if (player.inventory.firstEmpty() == -1) {
            player.location.world.dropItemNaturally(player.location, crateItemStack.clone().asQuantity(amount))
        } else {
            player.inventory.addItem(crateItemStack.clone().asQuantity(amount))
        }
    }

    fun selectReward() : Reward {
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

    private fun createCrateItemStack(description: List<String>) : ItemStack {
        val descriptionFormatted = ArrayList<String>()
        description.forEach loop@ { descriptionLine ->
            if (!descriptionLine.contains("{rewards}")) {
                descriptionFormatted.add(descriptionLine)
                return@loop
            }
            rewards.forEach {
                descriptionFormatted.add("&7- ${it.name.color()}")
            }
        }
        val nbtItem = NBTItem(ItemBuilder.from(Material.CHEST)
            .glow(true)
            .setName(name.color())
            .setLore(description.color())
            .build())
        nbtItem.setString("crate-id", id)

        return nbtItem.item
    }
}