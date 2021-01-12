package me.senseiju.commscraft.collectables.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.collectables.showCollectablesGui
import me.senseiju.commscraft.extensions.sendConfigMessage
import org.bukkit.entity.Player

@Command("Collectables")
class CollectablesCommand(private val plugin: CommsCraft) : CommandBase() {

    @Default
    fun onCommand(player: Player, @Optional targetName: String?) {
        if (targetName == null) {
            val user = plugin.userManager.userMap[player.uniqueId] ?: return
            showCollectablesGui(player, user)
            return
        }

        val targetPlayer = plugin.server.getPlayer(targetName)
        if (targetPlayer == null) {
            player.sendConfigMessage("CANNOT-FIND-TARGET")
            return
        }

        val user = plugin.userManager.userMap[player.uniqueId] ?: return
        showCollectablesGui(player, user)
    }
}