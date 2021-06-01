package me.senseiju.sennetmc.arena.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.arena.ArenaManager
import me.senseiju.sennetmc.arena.showArenaWagerGui
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.entity.Player

@Command("Duel")
class DuelCommand(private val plugin: SennetMC, private val arenaManager: ArenaManager) : CommandBase() {

    private val requests = arenaManager.requests

    @Default
    fun onCommand(sender: Player) {
        sender.sendConfigMessage("ARENA-HELP", false)
    }

    @SubCommand("Send")
    fun onSendCommand(sender: Player, @Completion("#players") targetPlayer: Player?) {
        if (targetPlayer == null) {
            sender.sendConfigMessage("CANNOT-FIND-TARGET")
            return
        }

        if (targetPlayer.uniqueId == sender.uniqueId) {
            sender.sendConfigMessage("ARENA-CANNOT-DUEL-SELF")
            return
        }

        if (arenaManager.isPlayerInQueue(sender) || arenaManager.isPlayerInCurrentMatch(sender) || doesPlayerHaveRequest(
                sender
            )
        ) {
            sender.sendConfigMessage("ARENA-ALREADY-QUEUED")
            return
        }

        if (arenaManager.isPlayerInQueue(targetPlayer) || arenaManager.isPlayerInCurrentMatch(targetPlayer) || doesPlayerHaveRequest(
                targetPlayer
            )
        ) {
            sender.sendConfigMessage("ARENA-TARGET-ALREADY-QUEUED")
            return
        }

        requests[sender.uniqueId] = targetPlayer.uniqueId

        sender.sendConfigMessage("ARENA-REQUEST-SENT")
        targetPlayer.sendConfigMessage("ARENA-REQUEST-RECEIVED", PlaceholderSet("{player}", sender.name))
    }

    @SubCommand("Accept")
    fun onAcceptSubCommand(accepter: Player) {
        val requesterUUID = requests.inverse()[accepter.uniqueId]

        if (requesterUUID == null) {
            accepter.sendConfigMessage("ARENA-NO-REQUEST")
            return
        }

        val requester = plugin.server.getPlayer(requesterUUID) ?: return

        showArenaWagerGui(requester, accepter)
    }

    @SubCommand("Cancel")
    fun onCancelSubCommand(sender: Player) {
        if (requests[sender.uniqueId] != null) {
            requests.remove(sender.uniqueId)

            sender.sendConfigMessage("ARENA-CANCELLED-REQUEST")
        }
    }

    @SubCommand("Deny")
    fun onDenySubCommand(sender: Player) {
        if (requests.inverse()[sender.uniqueId] != null) {

            sender.sendConfigMessage("ARENA-REQUEST-DENIED")

            plugin.server.getPlayer(requests.inverse().remove(sender.uniqueId) ?: return)
                ?.sendConfigMessage("ARENA-REQUEST-DENIED")
        }
    }

    private fun doesPlayerHaveRequest(player: Player): Boolean {
        return requests[player.uniqueId] != null || requests.inverse()[player.uniqueId] != null
    }
}