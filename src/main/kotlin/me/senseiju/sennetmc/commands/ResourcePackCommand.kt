package me.senseiju.sennetmc.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.entity.Player

@Command("Rp")
class ResourcePackCommand : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        player.sendConfigMessage("RESOURCE-PACK")
    }
}