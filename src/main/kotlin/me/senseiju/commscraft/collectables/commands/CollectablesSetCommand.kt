package me.senseiju.commscraft.collectables.commands

import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.PERMISSION_COLLECTABLES_SET
import me.senseiju.commscraft.collectables.CollectablesManager
import me.senseiju.commscraft.extensions.sendConfigMessage
import org.bukkit.entity.Player

@Command("CollectablesSet")
class CollectablesSetCommand(private val plugin: CommsCraft, private val collectablesManager: CollectablesManager) : CommandBase() {

    @Default
    @Permission(PERMISSION_COLLECTABLES_SET)
    fun onCommand(player: Player, targetName: String, collectableId: String) {
        val targetPlayer = plugin.server.getPlayer(targetName)
        if (targetPlayer == null) {
            player.sendConfigMessage("COLLECTABLES-CANNOT-FIND-TARGET")
            return
        }

        if (!collectablesManager.collectablesFile.config.getKeys(false).contains(collectableId)) {
            player.sendConfigMessage("COLLECTABLES-CANNOT-FIND-COLLECTABLE")
            return
        }

        collectablesManager.addCollectable(targetPlayer.uniqueId, collectableId, player)
    }
}