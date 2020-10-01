package me.senseiju.commscraft.collectables.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import org.bukkit.entity.Player

@Command("CollectablesRemove")
class CollectablesRemoveCommand(private val plugin: CommsCraft) : CommandBase() {
    private val collectablesManager = plugin.collectablesManager

    @Default
    @Permission("collectables.remove")
    fun onCommand(player: Player, targetName: String, collectableId: String) {
        val targetPlayer = plugin.server.getPlayer(targetName)
        if (targetPlayer == null) {
            plugin.sendMessage(player, "COLLECTABLES-CANNOT-FIND-TARGET")
            return
        }

        if (!plugin.collectablesFile.config.getKeys(false).contains(collectableId)) {
            plugin.sendMessage(player, "COLLECTABLES-CANNOT-FIND-COLLECTABLE")
            return
        }

        collectablesManager.removeCollectable(player, targetPlayer.uniqueId, collectableId)
    }
}