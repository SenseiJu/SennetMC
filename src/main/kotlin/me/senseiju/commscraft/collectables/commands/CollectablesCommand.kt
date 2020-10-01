package me.senseiju.commscraft.collectables.commands

import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.collectables.guis.CollectablesGui
import org.bukkit.entity.Player

@Command("Collectables")
class CollectablesCommand(private val plugin: CommsCraft) : CommandBase() {

    @Default
    fun onCommand(player: Player, @Optional targetName: String?) {
        if (targetName == null) {
            CollectablesGui.showCollectables(plugin, player)
            return
        }

        val targetPlayer = plugin.server.getPlayer(targetName)
        if (targetPlayer != null) {
            CollectablesGui.showCollectables(plugin, player, targetPlayer.uniqueId)
            return
        }

        plugin.sendMessage(player, "COLLECTABLES-CANNOT-FIND-TARGET")
    }
}