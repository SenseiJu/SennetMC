package me.senseiju.sennetmc.npcs.types.merchant.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_COMMANDS_SELL
import me.senseiju.sennetmc.npcs.types.merchant.Merchant
import org.bukkit.entity.Player

@Command("Sell")
class SellCommand(private val merchant: Merchant) : CommandBase() {

    @Default
    @Permission(PERMISSION_COMMANDS_SELL)
    fun onCommand(player: Player) {
        merchant.sellPlayersFish(player)
    }
}