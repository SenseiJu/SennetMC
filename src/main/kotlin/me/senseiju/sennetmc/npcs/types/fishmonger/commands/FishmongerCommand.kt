package me.senseiju.sennetmc.npcs.types.fishmonger.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_COMMANDS_FISHMONGER
import me.senseiju.sennetmc.npcs.types.fishmonger.openFishmongerGui
import org.bukkit.entity.Player

@Command("Fishmonger")
class FishmongerCommand : CommandBase() {

    @Default
    @Permission(PERMISSION_COMMANDS_FISHMONGER)
    fun onCommand(player: Player) {
        openFishmongerGui(player)
    }
}