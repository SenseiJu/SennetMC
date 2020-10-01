package me.senseiju.commscraft.collectables.commands

import kotlinx.coroutines.launch
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.entity.Player
import java.util.*

@Command("CollectablesSet")
class CollectablesSetCommand(private val plugin: CommsCraft) : CommandBase() {
    private val collectablesManager = plugin.collectablesManager

    @Default
    @Permission("collectables.set")
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

        collectablesManager.addCollectable(player, targetPlayer.uniqueId, collectableId)
    }
}