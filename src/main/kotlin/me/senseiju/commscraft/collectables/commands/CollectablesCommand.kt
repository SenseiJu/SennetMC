package me.senseiju.commscraft.collectables.commands

import kotlinx.coroutines.launch
import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.PERMISSION_COLLECTABLES_REMOVE
import me.senseiju.commscraft.PERMISSION_COLLECTABLES_SET
import me.senseiju.commscraft.collectables.CollectablesManager
import me.senseiju.commscraft.collectables.showCollectablesGui
import me.senseiju.commscraft.collectables.showCollectablesListGui
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("DUPLICATES")
@Command("Collectables")
class CollectablesCommand(private val plugin: CommsCraft, private val collectablesManager: CollectablesManager) : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        onShowSubCommand(player, null)
    }

    @SubCommand("show")
    fun onShowSubCommand(sender: Player, @Optional @Completion("#players") targetPlayer: Player?) {
        defaultScope.launch {
            if (targetPlayer == null) {
                val user = plugin.userManager.userMap[sender.uniqueId] ?: return@launch
                showCollectablesGui(sender, user)
                return@launch
            }

            val user = plugin.userManager.userMap[targetPlayer.uniqueId] ?: return@launch
            showCollectablesGui(sender, user)
        }
    }

    @SubCommand("list")
    fun onListSubCommand(sender: Player) {
        defaultScope.launch { showCollectablesListGui(sender) }
    }

    @SubCommand("set")
    @Permission(PERMISSION_COLLECTABLES_SET)
    fun onSetSubCommand(sender: CommandSender, targetPlayer: Player?, collectableId: String) {
        defaultScope.launch {
            if (targetPlayer == null) {
                sender.sendConfigMessage("CANNOT-FIND-TARGET")
                return@launch
            }

            if (!collectablesManager.collectablesFile.config.getKeys(false).contains(collectableId)) {
                sender.sendConfigMessage("COLLECTABLES-CANNOT-FIND-COLLECTABLE")
                return@launch
            }

            collectablesManager.addCollectable(targetPlayer.uniqueId, collectableId, sender)
        }
    }

    @SubCommand("remove")
    @Permission(PERMISSION_COLLECTABLES_REMOVE)
    fun onRemoveSubCommand(sender: CommandSender, targetPlayer: Player?, collectableId: String) {
        defaultScope.launch {
            if (targetPlayer == null) {
                sender.sendConfigMessage("CANNOT-FIND-TARGET")
                return@launch
            }

            if (!collectablesManager.collectablesFile.config.getKeys(false).contains(collectableId)) {
                sender.sendConfigMessage("COLLECTABLES-CANNOT-FIND-COLLECTABLE")
                return@launch
            }

            collectablesManager.removeCollectable(targetPlayer.uniqueId, collectableId, sender)
        }
    }

    @CompleteFor("set")
    fun completionForSetSubCommand(args: List<String>, sender: CommandSender) : List<String> {
        return when(args.size) {
            0 -> return plugin.server.onlinePlayers.map { it.name }
            1 -> return plugin.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[0]) }
            2 -> {
                val player = plugin.server.getPlayer(args[0]) ?: return emptyList()
                val user = plugin.userManager.userMap[player.uniqueId] ?: return emptyList()

                collectablesManager.collectables.keys.minus(user.collectables).toList()
            }
            else -> emptyList()
        }
    }

    @CompleteFor("remove")
    fun completionForRemoveSubCommand(args: List<String>, sender: CommandSender) : List<String> {
        return when(args.size) {
            0 -> return plugin.server.onlinePlayers.map { it.name }
            1 -> return plugin.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[0], true) }
            2 -> {
                val player = plugin.server.getPlayer(args[0]) ?: return emptyList()
                val user = plugin.userManager.userMap[player.uniqueId] ?: return emptyList()

                user.collectables
            }
            else -> emptyList()
        }
    }
}