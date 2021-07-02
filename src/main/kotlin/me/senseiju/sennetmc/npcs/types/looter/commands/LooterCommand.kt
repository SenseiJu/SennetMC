package me.senseiju.sennetmc.npcs.types.looter.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_COMMANDS_LOOTER
import me.senseiju.sennetmc.npcs.types.looter.openLooterGui
import org.bukkit.entity.Player

@Command("Looter")
class LooterCommand : CommandBase() {

    @Default
    @Permission(PERMISSION_COMMANDS_LOOTER)
    fun onCommand(player: Player) {
        openLooterGui(player)
    }
}