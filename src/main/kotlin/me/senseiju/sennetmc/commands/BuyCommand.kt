package me.senseiju.sennetmc.commands

import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.extensions.sendConfigMessage
import org.bukkit.entity.Player

@Command("Buy")
class BuyCommand : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        player.sendConfigMessage("BUYCRAFT-LINK")
    }
}