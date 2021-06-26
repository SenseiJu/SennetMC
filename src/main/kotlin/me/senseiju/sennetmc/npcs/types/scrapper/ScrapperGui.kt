package me.senseiju.sennetmc.npcs.types.scrapper

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.equipment.Equipment
import me.senseiju.sennetmc.equipment.fishing_net.createFishingNetItem
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.scrap.hasScrap
import me.senseiju.sennetmc.scrap.removeScrap
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.defaultScope
import me.senseiju.sennetmc.utils.extensions.defaultGuiTemplate
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.secondsToTimeFormat
import me.senseiju.sentils.extensions.color
import me.senseiju.sentils.extensions.entity.addItemOrDropNaturally
import me.senseiju.sentils.extensions.events.player
import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

private val npcType = NpcType.SCRAPPER
private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val scheduler = plugin.server.scheduler
private val equipmentFile = plugin.equipmentManager.equipmentFile
private val econ = plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider

fun showScrapperGui(scrapper: Scrapper, player: Player) {
    defaultScope.launch {
        val gui = defaultGuiTemplate(3, npcType.npcName)

        gui.setItem(2, 5, fishingNetGuiItem(scrapper, player))

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun fishingNetGuiItem(scrapper: Scrapper, player: Player): GuiItem {
    val lore = arrayListOf("")

    val currentCrafting = scrapper.crafting[player.uniqueId]?.get(Equipment.FISHING_NET)
    if (currentCrafting != null) {
        lore.add("&a&l** CRAFTING **")
        lore.add("")
        if (currentCrafting.finished) {
            lore.add("&a&lReady to collect!")
        } else {
            lore.add("&7Time remaining: &e${secondsToTimeFormat(currentCrafting.timeToComplete)}")
        }
    } else {
        lore.add("&7Throw the fishing net in the water to cast it")
        lore.add("&7and reap the large amount of rewards!")
    }

    return ItemBuilder.from(Equipment.FISHING_NET.material)
        .setName(Equipment.FISHING_NET.eqName)
        .setLore(lore.color())
        .asGuiItem {
            if (currentCrafting == null) {
                startCraftingEquipment(scrapper, player, Equipment.FISHING_NET)
                return@asGuiItem
            }

            if (currentCrafting.finished) {
                scrapper.crafting[player.uniqueId]?.remove(Equipment.FISHING_NET)

                val amount = equipmentFile.config.getInt("${Equipment.FISHING_NET}.crafting-amount", 3)
                it.player.inventory.addItemOrDropNaturally(createFishingNetItem(amount))
            } else {
                player.sendConfigMessage(
                    "SCRAPPER-ALREADY-CRAFTING",
                    false,
                    PlaceholderSet("{npcName}", NpcType.SCRAPPER.npcName)
                )
            }

            player.closeInventory()
            return@asGuiItem
        }
}

private fun startCraftingEquipment(scrapper: Scrapper, player: Player, equipment: Equipment) {
    val scrapCost = equipmentFile.config.getLong("${equipment}.crafting-scrap-cost", 100)
    val moneyCost = equipmentFile.config.getDouble("${equipment}.crafting-money-cost", 2500.0)
    val timeToComplete = equipmentFile.config.getLong("${equipment}.crafting-time", 3600)

    if (!player.inventory.hasScrap(scrapCost)) {
        player.sendConfigMessage("SCRAP-NOT-ENOUGH")
        return
    }

    if (econ?.has(player, moneyCost) == false) {
        player.sendConfigMessage("MONEY-NOT-ENOUGH")
        return
    }

    player.inventory.removeScrap(scrapCost)
    econ?.withdrawPlayer(player, moneyCost)

    val craftable = CraftableEquipment(equipment, player.uniqueId, timeToComplete)
    scrapper.crafting.computeIfAbsent(player.uniqueId) { EnumMap(Equipment::class.java) }[equipment] = craftable
    craftable.start(plugin)

    player.sendConfigMessage(
        "SCRAPPER-STARTED-CRAFTING",
        false,
        PlaceholderSet("{npcName}", NpcType.SCRAPPER.npcName)
        )

    player.closeInventory()
}