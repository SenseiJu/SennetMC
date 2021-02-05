package me.senseiju.sennetmc.arena

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.Gui
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.addItemOrDropNaturally
import me.senseiju.sennetmc.extensions.color
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.defaultScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val scheduler = plugin.server.scheduler
private val arenaManager = plugin.arenaManager

private val fillerSlots = listOf(3, 4, 5, 13, 21, 22, 23)
private val player1WagerSlots = listOf(0, 1, 2, 9, 10, 11, 18, 19, 20)
private val player2WagerSlots = listOf(6, 7, 8, 15, 16, 17, 24, 25, 26)

fun showArenaWagerGui(player1: Player, player2: Player) {
    defaultScope.launch {
        val gui = Gui(3, "&c&lArena wagers".color())

        gui.setItem(fillerSlots, createFillerGuiItem())
        gui.setDefaultClickAction {
            defaultClickAction(it, gui, player1, player2)
        }

        gui.setCloseGuiAction {
            if (it.reason == InventoryCloseEvent.Reason.PLAYER) {
                gui.close(player1)
                gui.close(player2)

                handleCancelWager(it.inventory, player1, player2)
            }
        }

        gui.setItem(12, createAcceptWagerGuiItem(gui, player1, player2, 14))
        gui.setItem(14, createAcceptWagerGuiItem(gui, player2, player1, 12))

        player1WagerSlots.forEach { slot ->
            gui.addSlotAction(slot) { playerWagerSlotAction(gui, it, player1, 12, player2, 14) }
        }

        player2WagerSlots.forEach { slot ->
            gui.addSlotAction(slot) { playerWagerSlotAction(gui, it, player2, 14, player1, 12) }
        }

        scheduler.runTask(plugin, Runnable {
            gui.open(player1)
            gui.open(player2)
        })
    }
}

private fun defaultClickAction(e: InventoryClickEvent, gui: Gui, player1: Player, player2: Player) {
    if (e.slot in player1WagerSlots || e.slot in player2WagerSlots) {
        val confirm1 = e.clickedInventory?.getItem(12)?.type
        val confirm2 = e.clickedInventory?.getItem(14)?.type

        if (confirm1 == Material.ORANGE_WOOL || confirm2 == Material.ORANGE_WOOL
            || confirm1 == Material.GREEN_WOOL || confirm2 == Material.GREEN_WOOL) {
            gui.updateItem(12, createAcceptWagerGuiItem(gui, player1, player2, 14))
            gui.updateItem(14, createAcceptWagerGuiItem(gui, player2, player1, 12))
        }
    }

    if (e.isShiftClick || e.hotbarButton != -1 || e.click == ClickType.SWAP_OFFHAND
        || e.currentItem?.type == Material.FISHING_ROD || e.currentItem?.type != Material.CHEST) {
        e.isCancelled = true
    }
}

private fun handleCancelWager(inventory: Inventory, player1: Player, player2: Player) {
    player1WagerSlots.forEach {
        player1.inventory.addItemOrDropNaturally(player1.location, inventory.getItem(it) ?: return@forEach)
    }

    player2WagerSlots.forEach {
        player2.inventory.addItemOrDropNaturally(player2.location, inventory.getItem(it) ?: return@forEach)
    }

    arenaManager.cancelRequest(player1)
}

private fun createFillerGuiItem() : GuiItem {
    return ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
            .setName(" ")
            .asGuiItem {
                it.isCancelled = true
            }
}

private fun playerWagerSlotAction(gui: Gui, e: InventoryClickEvent, player1: Player, playerConfirmSlot: Int, player2: Player, player2ConfirmSlot: Int) {
    if (e.whoClicked.uniqueId != player1.uniqueId) {
        e.isCancelled = true
        return
    }

    if (e.clickedInventory?.getItem(playerConfirmSlot)?.type == Material.ORANGE_WOOL) {
        createAcceptWagerGuiItem(gui, player1, player2, player2ConfirmSlot)
    }
}

private fun createAcceptWagerGuiItem(gui: Gui, player1: Player, player2: Player, player2ConfirmSlot: Int) : GuiItem {
    return ItemBuilder.from(Material.RED_WOOL)
            .setName("&bClick to accept".color())
            .asGuiItem {
                it.isCancelled = true

                if (it.whoClicked.uniqueId == player1.uniqueId) {
                    gui.updateItem(it.slot, createConfirmAcceptWagerGuiItem(gui, player1, player2, player2ConfirmSlot))
                }
            }
}

private fun createConfirmAcceptWagerGuiItem(gui: Gui, player1: Player, player2: Player, player2ConfirmSlot: Int) : GuiItem {
    return ItemBuilder.from(Material.ORANGE_WOOL)
            .setName("&bClick to confirm".color())
            .asGuiItem {
                it.isCancelled = true

                if (it.whoClicked.uniqueId == player1.uniqueId) {
                    when (it.clickedInventory?.getItem(player2ConfirmSlot)?.type) {
                        Material.ORANGE_WOOL -> gui.updateItem(it.slot, createConfirmedWagerGuiItem())
                        Material.GREEN_WOOL -> confirmedWager(it.clickedInventory!!, player1, player2)
                        else -> return@asGuiItem
                    }
                }
            }
}

private fun createConfirmedWagerGuiItem() : GuiItem {
    return ItemBuilder.from(Material.GREEN_WOOL)
            .setName("&c&lACCEPTED".color())
            .asGuiItem { e ->
                e.isCancelled = true
            }
}

private fun confirmedWager(inventory: Inventory, player1: Player, player2: Player) {
    player1.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
    player2.closeInventory(InventoryCloseEvent.Reason.PLUGIN)

    val player1Wager = player1WagerSlots.mapNotNull { inventory.getItem(it) }
    val player2Wager = player2WagerSlots.mapNotNull { inventory.getItem(it) }

    val arenaPlayer1 = ArenaPlayer(player1, player1Wager)
    val arenaPlayer2 = ArenaPlayer(player2, player2Wager)

    arenaManager.createMatch(arenaPlayer1, arenaPlayer2)

    player1.sendConfigMessage("ARENA-QUEUED")
    player2.sendConfigMessage("ARENA-QUEUED")
}