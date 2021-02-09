package me.senseiju.sennetmc.collectables.commands

import kotlinx.coroutines.launch
import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_COLLECTABLES_REMOVE
import me.senseiju.sennetmc.PERMISSION_COLLECTABLES_SET
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.collectables.CollectablesManager
import me.senseiju.sennetmc.collectables.showCollectablesGui
import me.senseiju.sennetmc.collectables.showCollectablesListGui
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.utils.defaultScope
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("DUPLICATES")
@Command("Collectables")
class CollectablesCommand(private val plugin: SennetMC, private val collectablesManager: CollectablesManager) : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        onShowSubCommand(player, player)
    }

    @SubCommand("show")
    fun onShowSubCommand(sender: Player, @Optional @Completion("#players") targetPlayer: Player?) {
        defaultScope.launch {
            if (targetPlayer == null) {
                sender.sendConfigMessage("CANNOT-FIND-TARGET")
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

            collectablesManager.removeCollectable(targetPlayer.uniqueId, collectableId, sender)
        }
    }

    @CompleteFor("set")
    fun completionForSetSubCommand(args: List<String>, sender: CommandSender) : List<String> {
        return when(args.size) {
            0 -> plugin.server.onlinePlayers.map { it.name }
            1 -> plugin.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[0], true) }
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
            0 -> plugin.server.onlinePlayers.map { it.name }
            1 -> plugin.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[0], true) }
            2 -> {
                val player = plugin.server.getPlayer(args[0]) ?: return emptyList()
                val user = plugin.userManager.userMap[player.uniqueId] ?: return emptyList()

                user.collectables
            }
            else -> emptyList()
        }
    }
}