package me.senseiju.sennetmc.crates.listeners

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.crates.CratesManager
import me.senseiju.sennetmc.extensions.dispatchCommands
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.ObjectSet
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class CrateOpenListener(private val plugin: SennetMC, private val cratesManager: CratesManager) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onPlayerRightClickCrate(e: PlayerInteractEvent) {
        if (e.item == null || !cratesManager.isItemCrate(e.item!!) || !e.action.name.contains("RIGHT_CLICK")
                || e.hand == EquipmentSlot.OFF_HAND) return

        val crate = cratesManager.getCrateFromItem(e.item!!) ?: return
        val reward = crate.selectRandomReward()
        reward.executeCommands(e.player)

        plugin.collectablesManager.addCollectable(e.player.uniqueId, crate.id)

        e.player.sendConfigMessage("CRATES-REWARD", false,
            ObjectSet("{crateName}", crate.name),
            ObjectSet("{rewardName}", reward.name))

        e.player.inventory.itemInMainHand.amount = e.player.inventory.itemInMainHand.amount - 1

        e.isCancelled = true
    }
}